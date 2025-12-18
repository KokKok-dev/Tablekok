package com.tablekok.store_service.presentation.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.store_service.application.dto.result.StoreWaitingInternalResult;

public record StoreWaitingInternalResponse(
	UUID storeId,
	UUID ownerId,
	LocalTime openTime,
	LocalTime closeTime,
	String storeName
) {
	public static StoreWaitingInternalResponse from(StoreWaitingInternalResult result) {
		return new StoreWaitingInternalResponse(
			result.storeId(),
			result.ownerId(),
			result.openTime(),
			result.closeTime(),
			result.storeName()
		);
	}
}
