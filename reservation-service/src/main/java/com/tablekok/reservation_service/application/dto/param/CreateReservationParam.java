package com.tablekok.reservation_service.application.dto.param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

public record CreateReservationParam(
	UUID userId,
	UUID storeId,
	ReservationDateTime reservationDateTime,
	Integer headcount,
	Integer deposit
) {
	public static CreateReservationParam of(UUID userId, UUID storeId, LocalDate reservationDate, LocalTime reservationTime, Integer headcount, Integer deposit) {
		return new CreateReservationParam(
			userId, storeId, ReservationDateTime.of(reservationDate, reservationTime), headcount, deposit);
	}

	public Reservation toEntity() {
		return Reservation.of(userId, storeId, reservationDateTime, headcount, deposit);
	}
}
