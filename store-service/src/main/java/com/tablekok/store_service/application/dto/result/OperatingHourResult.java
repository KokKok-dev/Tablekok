package com.tablekok.store_service.application.dto.result;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.store_service.domain.entity.OperatingHour;

public record OperatingHourResult(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {
	public static OperatingHourResult from(OperatingHour operatingHour) {
		return new OperatingHourResult(
			operatingHour.getDayOfWeek(),
			operatingHour.getOpenTime(),
			operatingHour.getCloseTime(),
			operatingHour.isClosed()
		);
	}
}
