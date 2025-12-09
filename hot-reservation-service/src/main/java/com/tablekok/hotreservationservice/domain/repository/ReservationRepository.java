package com.tablekok.hotreservationservice.domain.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.domain.entity.Reservation;

public interface ReservationRepository {
	// 예약 저장
	void save(Reservation newReservation);

	// 이미 그 시간대 예약이 있는지 확인용
	boolean existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
		UUID storeId, LocalDate reservationDate, LocalTime reservationTime);

}
