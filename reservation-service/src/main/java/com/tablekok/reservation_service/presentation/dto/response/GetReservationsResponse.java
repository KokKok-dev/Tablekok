package com.tablekok.reservation_service.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.tablekok.reservation_service.domain.entity.Reservation;

public record GetReservationsResponse(
	UUID storeId,
	LocalDate reservationDate,
	LocalTime reservationTime,
	Integer headcount,
	String reservationStatus
) {
	public static Page<GetReservationsResponse> toPage(Page<Reservation> reservations) {
		return reservations.map(reservation -> new GetReservationsResponse(
			reservation.getStoreId(),
			reservation.getReservationDateTime().getReservationDate(),
			reservation.getReservationDateTime().getReservationTime(),
			reservation.getHeadcount(),
			reservation.getReservationStatus().name()
		));

	}
}
