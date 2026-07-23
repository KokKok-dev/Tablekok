package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.hotreservationservice.domain.repository.CacheStore;
import com.tablekok.hotreservationservice.infrastructure.Cache.CacheStoreImpl;

/**
 * 스케줄러 입장 처리(processAllUsers)의 PUBLISH 발행 비용을 전/후로 측정하는 수동 성능 테스트.
 *
 * <p>측정 지표
 * <ul>
 *   <li>A. 틱 소요시간: processAllUsers(count) wall-clock (ms)</li>
 *   <li>B. PUBLISH 호출 수: CacheStore.convertAndSend 호출 횟수 (Mockito spy 로 카운트)</li>
 * </ul>
 *
 * <p>큐 사이즈마다 {@value #REPEAT} 회 반복 측정하고, 회차별 수치와 평균/최소/최대를 함께 남긴다.
 * 매 회차 시작 전 큐를 다시 시드하므로 각 회차는 동일 조건에서 측정된다.
 * (1회차는 JIT·커넥션 워밍업이 섞이므로 평균과 별개로 표시된다)
 *
 * <p>전제: 로컬 Redis 가 떠 있어야 한다(기본 localhost:6379 / pw systempass — dev 설정 기준).
 * 환경변수 REDIS_HOST / REDIS_PORT / REDIS_PASSWORD 로 덮어쓸 수 있다.
 *
 * <p>CI 에서 자동 실행되지 않도록 환경변수 RUN_REDIS_PERF=true 일 때만 활성화된다.
 * 실행 예 (PowerShell):
 * <pre>
 *   $env:RUN_REDIS_PERF="true"; ./gradlew :hot-reservation-service:test --tests "*QueueServicePublishPerfTest" --info
 * </pre>
 * 결과는 콘솔과 build/queue-perf-result.txt 양쪽에 남는다.
 */
@EnabledIfEnvironmentVariable(named = "RUN_REDIS_PERF", matches = "true")
class QueueServicePublishPerfTest {

	private static final String QUEUE_KEY = "reservation:queue";
	private static final String AVAILABLE_USERS_KEY = "reservation:available_users";
	private static final String PUB_SUB_CHANNEL = "waiting-queue";

	private static final long ENTRY_TTL = 30_000L;
	private static final int ENTRY_COUNT = 50; // 한 틱에 입장시키는 인원 (availableUserLimit 기준)
	static final int REPEAT = 10;              // 큐 사이즈당 반복 측정 횟수

	private LettuceConnectionFactory connectionFactory;
	private RedisTemplate<String, String> redisTemplate;

	@BeforeEach
	void setUp() {
		String host = envOrDefault("REDIS_HOST", "localhost");
		int port = Integer.parseInt(envOrDefault("REDIS_PORT", "6379"));
		String password = envOrDefault("REDIS_PASSWORD", "systempass");

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		if (password != null && !password.isBlank()) {
			config.setPassword(password);
		}
		connectionFactory = new LettuceConnectionFactory(config);
		connectionFactory.afterPropertiesSet();

		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
	}

	@AfterEach
	void tearDown() {
		if (redisTemplate != null) {
			redisTemplate.delete(List.of(QUEUE_KEY, AVAILABLE_USERS_KEY));
		}
		if (connectionFactory != null) {
			connectionFactory.destroy();
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {1_000, 5_000, 10_000})
	void measurePublishCost(int queueSize) {
		double[] elapsedMs = new double[REPEAT];
		long[] publishCalls = new long[REPEAT];

		for (int round = 0; round < REPEAT; round++) {
			// 1) 매 회차 큐 초기화 후 N명 재시드 (앞 회차의 입장 처리로 줄어든 큐를 원복)
			seedQueue(queueSize);

			// 2) 실제 Redis 를 쓰는 CacheStore + PUBLISH 카운트용 spy (회차별 카운트를 위해 매번 새로 생성)
			CacheStore store = Mockito.spy(newCacheStore());
			QueueService service = new QueueService(store, new ObjectMapper());
			ReflectionTestUtils.setField(service, "ENTRY_TTL", ENTRY_TTL);

			// 3) 측정: 틱 소요시간(A)
			long start = System.nanoTime();
			service.processAllUsers(ENTRY_COUNT);
			elapsedMs[round] = (System.nanoTime() - start) / 1_000_000.0;

			// 4) 측정: PUBLISH 호출 수(B)
			publishCalls[round] = Mockito.mockingDetails(store).getInvocations().stream()
				.filter(inv -> "convertAndSend".equals(inv.getMethod().getName()))
				.count();
		}

		record(queueSize, elapsedMs, publishCalls);
	}

	// 큐를 비우고 queueSize 명을 점수 오름차순으로 다시 채운다
	private void seedQueue(int queueSize) {
		redisTemplate.delete(List.of(QUEUE_KEY, AVAILABLE_USERS_KEY));
		for (int i = 0; i < queueSize; i++) {
			redisTemplate.opsForZSet().add(QUEUE_KEY, "user-" + i, i);
		}
	}

	private CacheStoreImpl newCacheStore() {
		CacheStoreImpl realStore = new CacheStoreImpl(redisTemplate);
		ReflectionTestUtils.setField(realStore, "QUEUE_KEY", QUEUE_KEY);
		ReflectionTestUtils.setField(realStore, "AVAILABLE_USERS_KEY", AVAILABLE_USERS_KEY);
		ReflectionTestUtils.setField(realStore, "PUB_SUB_CHANNEL", PUB_SUB_CHANNEL);
		return realStore;
	}

	private void record(int queueSize, double[] elapsedMs, long[] publishCalls) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[QUEUE-PERF] queueSize=%d  entryCount=%d  repeat=%d%n",
			queueSize, ENTRY_COUNT, REPEAT));

		// 회차별 수치
		for (int round = 0; round < REPEAT; round++) {
			sb.append(String.format("  round %2d: elapsedMs=%7.2f  publishCalls=%d%n",
				round + 1, elapsedMs[round], publishCalls[round]));
		}

		// 요약: 평균 / 최소 / 최대 (1회차는 워밍업이 섞이므로 제외한 평균도 함께)
		sb.append(String.format(
			"  ==> avg=%.2fms  min=%.2fms  max=%.2fms  avg(2~%d회차)=%.2fms  publishCalls=%d(고정)%n",
			average(elapsedMs, 0), min(elapsedMs), max(elapsedMs),
			REPEAT, average(elapsedMs, 1), publishCalls[0]));

		String report = sb.toString();
		System.out.print(report);
		try {
			Path out = Path.of("build", "queue-perf-result.txt");
			Files.createDirectories(out.getParent());
			Files.writeString(out, report,
				StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("[QUEUE-PERF] 결과 파일 기록 실패: " + e.getMessage());
		}
	}

	// fromIndex 회차부터의 평균 (0 이면 전체 평균, 1 이면 워밍업 1회차 제외)
	private static double average(double[] values, int fromIndex) {
		double sum = 0;
		for (int i = fromIndex; i < values.length; i++) {
			sum += values[i];
		}
		return sum / (values.length - fromIndex);
	}

	private static double min(double[] values) {
		double m = values[0];
		for (double v : values) {
			m = Math.min(m, v);
		}
		return m;
	}

	private static double max(double[] values) {
		double m = values[0];
		for (double v : values) {
			m = Math.max(m, v);
		}
		return m;
	}

	private static String envOrDefault(String key, String def) {
		String v = System.getenv(key);
		return (v == null || v.isBlank()) ? def : v;
	}
}
