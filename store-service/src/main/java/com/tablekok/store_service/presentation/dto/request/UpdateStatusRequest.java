package com.tablekok.store_service.presentation.dto.request;

import com.tablekok.store_service.application.dto.command.UpdateStoreStatusCommand;

import jakarta.validation.constraints.NotBlank;

public record UpdateStatusRequest(
	@NotBlank(message = "음식점 상태값 필수입니다.")
	String storeStatus
) {

	public UpdateStoreStatusCommand toCommand() {
		return UpdateStoreStatusCommand.builder()
			.storeStatus(storeStatus)
			.build();
	}
}
