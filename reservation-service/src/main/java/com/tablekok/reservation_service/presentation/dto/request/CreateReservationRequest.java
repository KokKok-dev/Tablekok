package com.tablekok.reservation_service.presentation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.reservation_service.application.dto.param.CreateReservationParam;

public record CreateReservationRequest(
	UUID storeId,
	LocalDate reservationDate,
	LocalTime reservationTime,
	Integer headcount,
	Integer deposit

) {
	public CreateReservationParam toParam(UUID userId) {
		return CreateReservationParam.of(userId, storeId, reservationDate, reservationTime, headcount, deposit);
	}
}
