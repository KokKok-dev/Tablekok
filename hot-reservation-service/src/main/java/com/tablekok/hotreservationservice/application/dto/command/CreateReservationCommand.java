package com.tablekok.hotreservationservice.application.dto.command;

import java.util.UUID;

import com.tablekok.hotreservationservice.domain.vo.ReservationDateTime;

import lombok.Builder;

@Builder
public record CreateReservationCommand(
	UUID userId,
	UUID storeId,
	ReservationDateTime reservationDateTime,
	Integer headcount,
	Integer deposit
) {
}
