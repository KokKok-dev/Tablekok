package com.tablekok.store_service.application.dto.param;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CreateOperatingHourParam(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {
}
