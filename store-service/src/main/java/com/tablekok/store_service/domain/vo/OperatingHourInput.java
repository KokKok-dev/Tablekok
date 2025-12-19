package com.tablekok.store_service.domain.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Builder;

@Builder
public record OperatingHourInput(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {
}
