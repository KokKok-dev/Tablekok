package com.tablekok.store_service.presentation.dto.response;

import java.util.UUID;

import lombok.Builder;

@Builder
public record GetCategoryResponse(
	UUID categoryId,
	String name
) {

	public static GetCategoryResponse from() {
		return GetCategoryResponse.builder()
			.categoryId(UUID.randomUUID())
			.name("양식")
			.build();
	}
}
