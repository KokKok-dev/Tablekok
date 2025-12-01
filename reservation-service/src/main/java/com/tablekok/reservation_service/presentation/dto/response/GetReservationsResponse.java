package com.tablekok.reservation_service.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.tablekok.reservation_service.domain.entity.Reservation;

import lombok.Builder;

@Builder
public record GetReservationsResponse(
	UUID storeId,
	LocalDate reservationDate,
	LocalTime reservationTime,
	Integer headcount,
	String reservationStatus
) {
	public static Page<GetReservationsResponse> toPage(Page<Reservation> reservations) {
		return reservations.map(reservation -> GetReservationsResponse.builder()
			.storeId(reservation.getStoreId())
			.reservationDate(reservation.getReservationDateTime().getReservationDate())
			.reservationTime(reservation.getReservationDateTime().getReservationTime())
			.headcount(reservation.getHeadcount())
			.reservationStatus(reservation.getReservationStatus().name())
			.build()
		);

	}
}
