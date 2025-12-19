package com.tablekok.reservation_service.application.dto.result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.tablekok.reservation_service.domain.entity.Reservation;

import lombok.Builder;

@Builder
public record GetReservationsForOwnerResult(
	UUID userId,
	UUID storeId,
	String reservationNumber,
	LocalDate reservationDate,
	LocalTime reservationTime,
	Integer headcount,
	Integer deposit,
	String reservationStatus
) {
	public static Page<GetReservationsForOwnerResult> toPage(Page<Reservation> reservations) {
		return reservations.map(reservation -> GetReservationsForOwnerResult.builder()
			.userId(reservation.getUserId())
			.storeId(reservation.getStoreId())
			.reservationNumber(reservation.getReservationNumber())
			.reservationDate(reservation.getReservationDateTime().getReservationDate())
			.reservationTime(reservation.getReservationDateTime().getReservationTime())
			.headcount(reservation.getHeadcount())
			.reservationStatus(reservation.getReservationStatus().name())
			.build()
		);
	}
}
