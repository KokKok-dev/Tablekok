package com.tablekok.reservation_service.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;

import lombok.Builder;

@Builder
public record CreateReservationResponse(
	String reservationNumber,
	LocalDate reservationDate,
	LocalTime reservationTime
) {
	public static CreateReservationResponse fromResult(CreateReservationResult result) {
		return CreateReservationResponse.builder()
			.reservationNumber(result.reservationNumber())
			.reservationDate(result.reservationDate())
			.reservationTime(result.reservationTime())
			.build();
	}
}
