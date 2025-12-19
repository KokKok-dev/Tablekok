package com.tablekok.reservation_service.presentation.dto.response;

import java.util.UUID;

import com.tablekok.reservation_service.application.dto.result.GetReservationResult;

import lombok.Builder;

@Builder
public record GetReservationResponse(
	UUID reservationId,
	UUID storeId,
	UUID userId,
	String reservationStatus
) {
	public static GetReservationResponse fromResult(GetReservationResult result) {
		return GetReservationResponse.builder()
			.reservationId(result.reservationId())
			.storeId(result.storeId())
			.userId(result.userId())
			.reservationStatus(result.reservationStatus())
			.build();
	}
}
