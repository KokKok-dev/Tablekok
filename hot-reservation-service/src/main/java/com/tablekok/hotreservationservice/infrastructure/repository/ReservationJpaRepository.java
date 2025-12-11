package com.tablekok.hotreservationservice.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.hotreservationservice.domain.entity.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {

	boolean existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
		UUID storeId,
		LocalDate reservationDate,
		LocalTime reservationTime
	);

}
