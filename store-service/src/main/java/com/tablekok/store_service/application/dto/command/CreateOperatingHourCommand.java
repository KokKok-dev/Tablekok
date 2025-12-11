package com.tablekok.store_service.application.dto.command;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.vo.OperatingHourInput;

public record CreateOperatingHourCommand(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {

	public OperatingHour toEntity(Store store) {
		return OperatingHour.of(store, dayOfWeek, openTime, closeTime, isClosed);
	}

	public OperatingHourInput toOperatingHourInput() {
		return OperatingHourInput.builder()
			.dayOfWeek(dayOfWeek)
			.openTime(openTime)
			.closeTime(closeTime)
			.isClosed(isClosed)
			.build();

	}
}
