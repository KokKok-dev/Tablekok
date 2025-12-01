package com.tablekok.reservation_service.presentation.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.reservation_service.application.dto.param.CreateReservationParam;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateReservationRequest(

	@NotNull(message = "가게 ID는 필수입니다.")
	UUID storeId,

	@NotNull(message = "예약 날짜는 필수입니다.")
	@FutureOrPresent(message = "예약 날짜는 오늘 또는 미래 날짜여야 합니다.")
	LocalDate reservationDate,

	@NotNull(message = "예약 시간은 필수입니다.")
	LocalTime reservationTime,

	@NotNull(message = "예약 인원은 필수입니다.")
	@Min(value = 1, message = "예약 인원은 최소 1명이어야 합니다.")
	Integer headcount,

	Integer deposit

) {
	// 서비스로 전달할 파람으로
	public CreateReservationParam toParam(UUID userId) {
		return CreateReservationParam.builder()
			.userId(userId)
			.storeId(storeId)
			.reservationDateTime(ReservationDateTime.builder()
				.reservationDate(reservationDate)
				.reservationTime(reservationTime)
				.build()
			)
			.headcount(headcount)
			.deposit(deposit)
			.build();
	}
}
