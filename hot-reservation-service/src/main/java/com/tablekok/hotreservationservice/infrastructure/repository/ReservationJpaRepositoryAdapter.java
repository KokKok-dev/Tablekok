package com.tablekok.hotreservationservice.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tablekok.hotreservationservice.domain.entity.Reservation;
import com.tablekok.hotreservationservice.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationJpaRepositoryAdapter implements ReservationRepository {
	private final ReservationJpaRepository reservationJpaRepository;

	@Override
	public void save(Reservation newReservation) {
		reservationJpaRepository.save(newReservation);
	}

	@Override
	public boolean existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
		UUID storeId, LocalDate reservationDate, LocalTime reservationTime) {
		return reservationJpaRepository.existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
			storeId, reservationDate, reservationTime);
	}

}
