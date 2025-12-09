package com.tablekok.store_service.application.dto.command;

import java.util.UUID;

import lombok.Builder;

@Builder
public record UpdateStoreReservationPolicyStatusCommand(
	UUID ownerId,
	UUID storeId,
	Boolean isActive
) {
}
