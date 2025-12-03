package com.tablekok.reservation_service.application.dto.result;

import java.util.UUID;

import com.tablekok.reservation_service.domain.entity.Reservation;

import lombok.Builder;

@Builder
public record GetReservationResult(
	UUID reservationId,
	UUID storeId,
	UUID userId,
	String reservationStatus
) {
	public static GetReservationResult of(Reservation reservation) {
		return GetReservationResult.builder()
			.reservationId(reservation.getId())
			.storeId(reservation.getStoreId())
			.userId(reservation.getUserId())
			.reservationStatus(reservation.getReservationStatus().name())
			.build();
	}
}
