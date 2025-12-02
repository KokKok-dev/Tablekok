package com.tablekok.store_service.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateOperatingHourRequest(
	@NotBlank(message = "요일 정보는 필수입니다.")
	String day,

	// HH:MM 형식 (예: 10:00). 시, 분만 받아 Service에서 LocalTime으로 변환
	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시간 형식은 HH:MM이어야 합니다.")
	@NotBlank(message = "오픈 시간은 필수입니다.")
	String openTime,

	@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시간 형식은 HH:MM이어야 합니다.")
	@NotBlank(message = "마감 시간은 필수입니다.")
	String closeTime,

	@NotNull(message = "휴무일 여부는 필수입니다.")
	boolean isClosed
) {
}
