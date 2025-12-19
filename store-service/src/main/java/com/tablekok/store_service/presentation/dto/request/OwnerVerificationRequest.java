package com.tablekok.store_service.presentation.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record OwnerVerificationRequest(
	@NotNull(message = "store ID 입력은 필수입니다.")
	UUID storeId,
	@NotNull(message = "owner ID 입력은 필수입니다.")
	UUID ownerId
) {
}
