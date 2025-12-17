package com.tablekok.store_service.application.dto.command;

import java.util.UUID;

import lombok.Builder;

@Builder
public record UpdateStoreStatusCommand(
	UUID storeId,
	String storeStatus,
	String userRole
) {
}
