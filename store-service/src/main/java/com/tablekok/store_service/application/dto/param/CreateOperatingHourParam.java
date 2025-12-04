package com.tablekok.store_service.application.dto.param;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;

public record CreateOperatingHourParam(
	DayOfWeek dayOfWeek,
	LocalTime openTime,
	LocalTime closeTime,
	boolean isClosed
) {

	public OperatingHour toEntity(Store store) {
		return OperatingHour.of(store, dayOfWeek, openTime, closeTime, isClosed);
	}

}
