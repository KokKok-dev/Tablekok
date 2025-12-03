package com.tablekok.review_service.application.client.dto;

import java.util.UUID;

import com.tablekok.review_service.domain.vo.Reservation;

public record GetReservationResponse(
	UUID reservationId,
	UUID storeId,
	UUID userId,
	String reservationStatus
) {
	public Reservation toVo() {
		return Reservation.of(
			reservationId,
			storeId,
			userId,
			reservationStatus
		);
	}
}
