package com.tablekok.reservation_service.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.service.ReservationErrorCode;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {
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

	@Override
	public Reservation findByIdAndUserId(UUID reservationId, UUID userId) {
		return reservationJpaRepository.findByIdAndUserId(reservationId, userId).orElseThrow(() ->
			new AppException(ReservationErrorCode.RESERVATION_NOT_FOUND));
	}

	@Override
	public Reservation findById(UUID reservationId) {
		return reservationJpaRepository.findById(reservationId).orElseThrow(() ->
			new AppException(ReservationErrorCode.RESERVATION_NOT_FOUND));
	}

	@Override
	public Page<Reservation> findByUserId(UUID userId, Pageable pageable) {
		return reservationJpaRepository.findByUserId(userId, pageable);
	}

	@Override
	public Page<Reservation> findByStoreId(UUID storeId, Pageable pageable) {
		return reservationJpaRepository.findByStoreId(storeId, pageable);
	}

}
