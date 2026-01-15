package com.tablekok.reservation_service.domain.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tablekok.reservation_service.domain.entity.Reservation;

public interface ReservationRepository {
	// 예약 저장
	void save(Reservation newReservation);

	// 이미 그 시간대 예약이 있는지 확인용
	boolean existsByStoreIdAndReservationDateTime_ReservationDateAndReservationDateTime_ReservationTime(
		UUID storeId, LocalDate reservationDate, LocalTime reservationTime);

	// 고객 본인의 예약을 가져옴
	Reservation findByIdAndUserId(UUID reservationId, UUID userId);

	// 아이디로 예약 가져옴
	Reservation findById(UUID reservationId);

	// 고객이 예약한 모든 예약 조회
	Page<Reservation> findByUserId(UUID userId, Pageable normalizedPageable);

	// 해당 음식점의 모든 예약 조회
	Page<Reservation> findByStoreId(UUID storeId, Pageable normalizedPageable);

	// 해당 식당의 특정 일 예약 목록 조회
	List<Reservation> findByStoreIdAndReservationDateTime_ReservationDate(UUID storeId, LocalDate date);
}
