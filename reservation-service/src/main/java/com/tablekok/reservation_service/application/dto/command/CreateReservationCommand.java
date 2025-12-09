package com.tablekok.reservation_service.application.dto.command;

import java.util.UUID;

import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

import lombok.Builder;

@Builder
public record CreateReservationCommand(
	UUID userId,
	UUID storeId,
	ReservationDateTime reservationDateTime,
	Integer headcount,
	Integer deposit
) {
	// Reservation 엔티티로
	public Reservation toEntity() {
		return Reservation.of(userId, storeId, reservationDateTime, headcount, deposit);
	}
}
