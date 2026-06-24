package com.tablekok.hotreservationservice.domain.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.domain.entity.Reservation;

public interface ReservationRepository {
	// 예약 저장
	void save(Reservation newReservation);

	// 활성 예약(취소/거절/삭제 제외) 중 동일 슬롯이 있는지 확인용
	boolean existsActiveReservation(
		UUID storeId, LocalDate reservationDate, LocalTime reservationTime);

}
