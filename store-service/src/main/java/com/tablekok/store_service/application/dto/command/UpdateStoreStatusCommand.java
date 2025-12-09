package com.tablekok.store_service.application.dto.command;

import lombok.Builder;

@Builder
public record UpdateStoreStatusCommand(
	String storeStatus
) {
}
