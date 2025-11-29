package com.tablekok.store_service.presentation.dto.response;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CategoryResponse(
	UUID categoryId,
	String name
) {

	public static CategoryResponse from() {
		return CategoryResponse.builder()
			.categoryId(UUID.randomUUID())
			.name("양식")
			.build();
	}
}
