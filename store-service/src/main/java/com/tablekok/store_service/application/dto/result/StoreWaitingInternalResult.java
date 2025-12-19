package com.tablekok.store_service.application.dto.result;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;

public record StoreWaitingInternalResult(
	UUID storeId,
	UUID ownerId,
	LocalTime openTime,
	LocalTime closeTime,
	String storeName
) {
	public static StoreWaitingInternalResult from(Store store, OperatingHour hour) {
		return new StoreWaitingInternalResult(
			store.getId(),
			store.getOwnerId(),
			hour.getOpenTime(),
			hour.getCloseTime(),
			store.getName()
		);
	}
}
