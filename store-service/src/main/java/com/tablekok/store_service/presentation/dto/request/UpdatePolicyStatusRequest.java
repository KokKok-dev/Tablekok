package com.tablekok.store_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.store_service.application.dto.command.UpdateStoreReservationPolicyStatusCommand;

import jakarta.validation.constraints.NotNull;

public record UpdatePolicyStatusRequest(
	@NotNull
	Boolean isActive
) {

	public UpdateStoreReservationPolicyStatusCommand toCommand(UUID ownerId, UUID storeId) {
		return UpdateStoreReservationPolicyStatusCommand.builder()
			.ownerId(ownerId)
			.storeId(storeId)
			.isActive(isActive)
			.build();
	}
}
