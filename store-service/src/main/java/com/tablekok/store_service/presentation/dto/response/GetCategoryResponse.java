package com.tablekok.store_service.presentation.dto.response;

import java.util.UUID;

import com.tablekok.store_service.domain.entity.Category;

import lombok.Builder;

@Builder
public record GetCategoryResponse(
	UUID categoryId,
	String name
) {

	public static GetCategoryResponse from(Category category) {
		return GetCategoryResponse.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.build();
	}
}
