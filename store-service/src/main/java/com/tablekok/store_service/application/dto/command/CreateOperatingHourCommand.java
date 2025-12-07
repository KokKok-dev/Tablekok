package com.tablekok.store_service.application.dto.command;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.store_service.domain.entity.OperatingHour;

public record CreateOperatingHourCommand(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {

	public OperatingHour toEntity() {
		return OperatingHour.of(dayOfWeek, openTime, closeTime, isClosed);
	}

}
