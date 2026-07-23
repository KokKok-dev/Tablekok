package com.tablekok.reservation_service.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.tablekok.reservation_service.application.client.StoreClient;
import com.tablekok.reservation_service.application.dto.command.CreateReservationCommand;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;
import com.tablekok.reservation_service.infrastructure.repository.ReservationJpaRepository;

/**
 * 분산락 동시성 테스트 — 같은 슬롯에 N명이 동시에 예약해도 "딱 1건만" 성공하는지 검증.
 *
 * - 실제 Redis(Testcontainers)로 Redisson 분산락을 띄운다.
 * - DB 유니크 제약은 적용하지 않는다(엔티티 스키마만) → 1건만 생기면 "막은 건 락"으로 확정된다.
 * - 락이 걸린 ReservationCommitter.commit()을 직접 동시 호출한다(외부 호출/정책 검증은 락 밖이라 제외).
 *
 * ⚠️ 실행에 Docker 필요. (Docker 데몬이 떠 있어야 Redis 컨테이너가 기동됨)
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지정한 H2를 그대로 사용(임베디드 교체 끔)
@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(properties = {
	// 디스커버리/트레이싱 비활성화
	"eureka.client.enabled=false",
	"spring.cloud.discovery.enabled=false",
	"management.tracing.enabled=false",
	// H2 데이터소스 (부분 유니크 인덱스 없음 → 락만 검증)
	"spring.datasource.url=jdbc:h2:mem:locktest;DB_CLOSE_DELAY=-1",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
class ReservationCommitterConcurrencyTest {

	private static final String REDIS_PASSWORD = "testpass";

	@Container
	static final GenericContainer<?> REDIS =
		new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
			.withExposedPorts(6379)
			.withCommand("redis-server", "--requirepass", REDIS_PASSWORD); // 앱이 AUTH를 보내므로 컨테이너도 비번 요구

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS::getHost);
		registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
		registry.add("spring.data.redis.password", () -> REDIS_PASSWORD);
	}

	private static final UUID STORE = UUID.randomUUID();
	private static final LocalDate DATE = LocalDate.now().plusDays(1);
	private static final LocalTime TIME = LocalTime.of(18, 0);

	// commit()은 StoreClient를 호출하지 않음. 컨텍스트 기동 시 실제 Feign 빈 생성을 피하려 mock으로 대체.
	@MockitoBean
	private StoreClient storeClient;

	@Autowired
	private ReservationCommitter reservationCommitter;
	@Autowired
	private ReservationJpaRepository reservationJpaRepository;

	@Test
	void 같은_슬롯에_200명이_동시에_예약해도_1건만_성공한다() throws InterruptedException {
		int concurrentUsers = 200;
		ExecutorService pool = Executors.newFixedThreadPool(concurrentUsers);
		CountDownLatch ready = new CountDownLatch(concurrentUsers); // 전원 준비 대기
		CountDownLatch start = new CountDownLatch(1);               // 동시 출발 신호
		AtomicInteger success = new AtomicInteger();
		AtomicInteger failure = new AtomicInteger();
		Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

		for (int i = 0; i < concurrentUsers; i++) {
			pool.execute(() -> {
				ready.countDown();
				try {
					start.await();                          // 모두 여기서 멈췄다가
					reservationCommitter.commit(command()); // 동시에 같은 슬롯 예약 시도
					success.incrementAndGet();
				} catch (Exception e) {
					failure.incrementAndGet();              // 중복/락대기초과 등은 실패로 집계
					errors.add(e);                          // 실패 원인 수집(진단용)
				}
			});
		}

		ready.await();        // 200개 스레드가 모두 출발선에 섰는지 확인
		start.countDown();    // 동시 출발
		pool.shutdown();
		boolean finished = pool.awaitTermination(60, TimeUnit.SECONDS);

		// 실패 예외 종류 — 정상이면 "중복 예약" 예외만 있어야 함. 진단 위해 메시지에 노출.
		List<String> distinctErrors = errors.stream()
			.map(e -> e.getClass().getSimpleName() + ": " + e.getMessage())
			.distinct()
			.toList();

		assertThat(finished).isTrue();
		assertThat(success.get())
			.as("성공은 1건이어야 함. 실제 실패 예외 종류=%s", distinctErrors)
			.isEqualTo(1);
		assertThat(reservationJpaRepository.count())
			.as("DB에는 1건만 저장돼야 함")
			.isEqualTo(1);
		assertThat(failure.get()).isEqualTo(concurrentUsers - 1);    // 나머지는 실패
	}

	private CreateReservationCommand command() {
		return CreateReservationCommand.builder()
			.userId(UUID.randomUUID())                                       // 신청자는 제각각
			.storeId(STORE)                                                  // 같은 가게
			.reservationDateTime(ReservationDateTime.of(LocalDateTime.of(DATE, TIME))) // 같은 슬롯
			.headcount(2)
			.deposit(0)                                                      // deposit 0 → RESERVED 상태
			.build();
	}
}
