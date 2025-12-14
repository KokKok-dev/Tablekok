package com.tablekok.reservation_service.application.client.dto.request;

import java.util.UUID;

import lombok.Builder;

@Builder
public record OwnerVerificationRequest(
	UUID storeId,
	UUID ownerId
) {
	public static OwnerVerificationRequest of(UUID storeId, UUID ownerId) {
		return OwnerVerificationRequest.builder()
			.storeId(storeId)
			.ownerId(ownerId)
			.build();
	}
}
