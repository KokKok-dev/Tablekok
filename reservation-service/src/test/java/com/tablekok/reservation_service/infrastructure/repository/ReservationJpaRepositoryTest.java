package com.tablekok.reservation_service.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.entity.ReservationStatus;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

/**
 * 활성 예약 기준 쿼리 검증 — 중복 판정(existsActiveReservation)과
 * 예약 가능 시간 조회(findActiveByStoreIdAndDate) 모두 취소/거절/삭제 예약을 제외하는지 확인.
 * 임베디드 H2 슬라이스 테스트.
 */
@DataJpaTest
@ActiveProfiles("test")
class ReservationJpaRepositoryTest {

	private static final UUID STORE = UUID.randomUUID();
	private static final UUID USER = UUID.randomUUID();
	private static final LocalDate DATE = LocalDate.of(2026, 7, 1);
	private static final LocalTime TIME = LocalTime.of(18, 0);

	@Autowired
	private ReservationJpaRepository repository;
	@Autowired
	private TestEntityManager em;

	@Test
	void 활성_예약이_있으면_중복으로_차단된다() {
		save(reservation(DATE, TIME)); // RESERVED

		boolean exists = repository.existsActiveReservation(
			STORE, DATE, TIME, ReservationStatus.SLOT_FREEING);

		assertThat(exists).isTrue();
	}

	@Test
	void 취소되거나_거절된_예약만_있으면_재예약이_허용된다() {
		Reservation canceled = reservation(DATE, TIME);
		canceled.cancel();
		Reservation rejected = reservation(DATE, TIME);
		rejected.reject();
		save(canceled);
		save(rejected);

		boolean exists = repository.existsActiveReservation(
			STORE, DATE, TIME, ReservationStatus.SLOT_FREEING);

		assertThat(exists).isFalse();
	}

	@Test
	void 다른_시간대_예약은_중복으로_보지_않는다() {
		save(reservation(DATE, LocalTime.of(20, 0))); // 20:00 활성 예약

		boolean exists = repository.existsActiveReservation(
			STORE, DATE, TIME, ReservationStatus.SLOT_FREEING); // 18:00 조회

		assertThat(exists).isFalse();
	}

	@Test
	void 예약가능시간_조회는_취소_거절된_예약을_제외한다() {
		save(reservation(DATE, LocalTime.of(18, 0)));            // 활성
		Reservation canceled = reservation(DATE, LocalTime.of(19, 0));
		canceled.cancel();                                       // 취소 → 제외 대상
		save(canceled);

		List<Reservation> result = repository.findActiveByStoreIdAndDate(
			STORE, DATE, ReservationStatus.SLOT_FREEING);

		assertThat(result)
			.extracting(r -> r.getReservationDateTime().getReservationTime())
			.containsExactly(LocalTime.of(18, 0));               // 취소된 19:00은 빠짐
	}

	private Reservation reservation(LocalDate date, LocalTime time) {
		return Reservation.create(
			USER, STORE, ReservationDateTime.of(LocalDateTime.of(date, time)), 2, 0);
	}

	private void save(Reservation reservation) {
		em.persist(reservation);
		em.flush();
	}
}
