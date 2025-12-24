package com.tablekok.reservation_service.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.reservation_service.domain.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {

	boolean existsByStoreIdAndReservationDateTime_ReservationDateAndReservationDateTime_ReservationTime(
		UUID storeId,
		LocalDate reservationDate,
		LocalTime reservationTime
	);

	Optional<Reservation> findByIdAndUserId(UUID reservationId, UUID userId);

	Page<Reservation> findByUserId(UUID userId, Pageable pageable);

	Page<Reservation> findByStoreId(UUID storeId, Pageable pageable);

	List<Reservation> findByStoreIdAndReservationDateTime_ReservationDate(UUID storeId, LocalDate date);
}
