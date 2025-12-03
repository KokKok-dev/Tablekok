package com.tablekok.store_service.presentation.dto.response;

import java.util.UUID;

import com.tablekok.store_service.application.dto.result.FindCategoryResult;

import lombok.Builder;

@Builder
public record GetCategoryResponse(
	UUID categoryId,
	String name
) {

	public static GetCategoryResponse from(FindCategoryResult result) {
		return GetCategoryResponse.builder()
			.categoryId(result.categoryId())
			.name(result.name())
			.build();
	}
}
