package com.tablekok.store_service.presentation.dto.request;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOperatingHourRequest(
	@NotBlank(message = "요일 정보는 필수입니다.")
	String day,

	@DateTimeFormat(pattern = "HH:mm")
	LocalTime openTime,

	@DateTimeFormat(pattern = "HH:mm")
	LocalTime closeTime,

	@NotNull(message = "휴무일 여부는 필수입니다.")
	boolean isClosed
) {
}
