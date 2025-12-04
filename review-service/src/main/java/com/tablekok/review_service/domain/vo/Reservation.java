package com.tablekok.review_service.domain.vo;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record Reservation(
	UUID reservationId,
	UUID storeId,
	UUID userId,
	String reservationStatus
) {
	public static Reservation of(
		UUID reservationId,
		UUID storeId,
		UUID userId,
		String reservationStatus
	) {
		return Reservation.builder()
			.reservationId(reservationId)
			.storeId(storeId)
			.userId(userId)
			.reservationStatus(reservationStatus)
			.build();
	}
}
