package com.tablekok.waiting_server.infrastructure.client.dto;

import java.time.LocalTime;
import java.util.UUID;

public record StoreWaitingInternalResponse(
	UUID storeId,
	UUID ownerId,
	LocalTime openTime,
	LocalTime closeTime,
	String storeName
) {
}
