package com.tablekok.reservation_service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tablekok.reservation_service.application.dto.command.CreateReservationCommand;
import com.tablekok.reservation_service.application.service.ReservationService;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

@SpringBootTest
class ReservationConcurrencyTest {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("락이_없으면_동시에_100명_예약_시_중복이_발생한다")
	void concurrency_test_without_lock() throws InterruptedException {

		int threadCount = 100;
		//32개의 비동기 스레드풀 생성
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		CountDownLatch latch = new CountDownLatch(threadCount);

		CreateReservationCommand command = CreateReservationCommand.builder()
			.userId(UUID.fromString("2a34bc0d-21c1-4381-b892-4b1123bb964f"))
			.storeId(UUID.fromString("a0c33cc2-bafc-4031-8173-c95ad7fb7621"))
			.reservationDateTime(ReservationDateTime.of(LocalDateTime.of(2026, 1, 15, 18, 0)))
			.headcount(4)
			.build();

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					reservationService.createReservation(command);
				} catch (Exception e) {
					System.out.println("에러 발생: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(); // 100개 완료될 때까지 대기

		LocalDate targetDate = command.reservationDateTime().getReservationDate();
		LocalTime targetTime = command.reservationDateTime().getReservationTime();
		UUID targetStoreId = command.storeId();

		Integer duplicateCount = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM p_reservation WHERE store_id = ? AND reservation_date = ? AND reservation_time = ?",
			Integer.class,
			targetStoreId,
			targetDate,
			targetTime
		);

		assertThat(duplicateCount)
			.withFailMessage("동시성 이슈가 발생하였습니다.")
			.isEqualTo(1);
	}
}
