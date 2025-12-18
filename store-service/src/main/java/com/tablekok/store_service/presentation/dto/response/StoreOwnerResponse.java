package com.tablekok.store_service.presentation.dto.response;

import java.util.UUID;

public record StoreOwnerResponse(
	UUID storeId,
	UUID ownerId
) {
}
