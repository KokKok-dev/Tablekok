package com.tablekok.reservation_service.presentation.dto.response;

import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;

import lombok.Builder;

@Builder
public record CreateReservationResponse(
	String reservationNumber,
	String reservationDateTime
) {
	public static CreateReservationResponse fromResult(CreateReservationResult result) {
		String dateTime = String.format("%s년 %s월 %s일 %s시 %s분",
			result.reservationDate().getYear(),
			result.reservationDate().getMonthValue(),
			result.reservationDate().getDayOfMonth(),
			result.reservationTime().getHour(),
			result.reservationTime().getMinute()
		);

		return CreateReservationResponse.builder()
			.reservationNumber(result.reservationNumber())
			.reservationDateTime(dateTime)
			.build();
	}
}
