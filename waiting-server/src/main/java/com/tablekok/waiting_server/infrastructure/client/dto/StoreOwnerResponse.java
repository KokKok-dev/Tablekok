package com.tablekok.waiting_server.infrastructure.client.dto;

import java.util.UUID;

public record StoreOwnerResponse(
	UUID storeId,
	UUID ownerId
) {
}
