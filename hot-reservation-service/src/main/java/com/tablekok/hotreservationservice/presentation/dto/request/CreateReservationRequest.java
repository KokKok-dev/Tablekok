package com.tablekok.hotreservationservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.application.dto.command.CreateReservationCommand;
import com.tablekok.hotreservationservice.domain.vo.ReservationDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
	@NotNull(message = "가게 ID는 필수입니다.")
	UUID storeId,

	@NotNull(message = "예약 날짜, 시간은 필수입니다.")
	@FutureOrPresent(message = "예약 날짜는 오늘 또는 미래 날짜여야 합니다.")
	LocalDateTime reservationDateTime,

	@NotNull(message = "예약 인원은 필수입니다.")
	@Min(value = 1, message = "예약 인원은 최소 1명이어야 합니다.")
	Integer headcount,

	Integer deposit

) {
	// 서비스로 전달할 command로
	public CreateReservationCommand toCommand(String strUserId) {
		return CreateReservationCommand.builder()
			.userId(UUID.fromString(strUserId))
			.storeId(storeId)
			.reservationDateTime(ReservationDateTime.of(reservationDateTime))
			.headcount(headcount)
			.deposit(deposit)
			.build();
	}
}
