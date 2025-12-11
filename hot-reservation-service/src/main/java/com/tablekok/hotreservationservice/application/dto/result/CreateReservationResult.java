package com.tablekok.hotreservationservice.application.dto.result;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.domain.entity.Reservation;

import lombok.Builder;

@Builder
public record CreateReservationResult(
	UUID reservationId,
	String reservationNumber,
	LocalDate reservationDate,
	LocalTime reservationTime
) {
	public static CreateReservationResult of(Reservation reservation) {
		return CreateReservationResult.builder()
			.reservationId(reservation.getId())
			.reservationNumber(reservation.getReservationNumber())
			.reservationDate(reservation.getReservationDateTime().getReservationDate())
			.reservationTime(reservation.getReservationDateTime().getReservationTime())
			.build();
	}
}
