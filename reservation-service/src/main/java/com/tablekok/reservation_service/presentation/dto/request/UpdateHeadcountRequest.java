package com.tablekok.reservation_service.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateHeadcountRequest(

	@NotNull(message = "예약 인원은 필수입니다.")
	@Min(value = 1, message = "예약 인원은 최소 1명이어야 합니다.")
	Integer headcount
) {
}
