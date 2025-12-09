package com.tablekok.hotreservationservice.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.hotreservationservice.domain.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {

	boolean existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
		UUID storeId,
		LocalDate reservationDate,
		LocalTime reservationTime
	);

	Optional<Reservation> findByIdAndUserId(UUID reservationId, UUID userId);

	Page<Reservation> findByUserId(UUID userId, Pageable pageable);

	Page<Reservation> findByStoreId(UUID storeId, Pageable pageable);
}
