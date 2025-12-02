package com.tablekok.store_service.application.dto.result;

import java.util.UUID;

import com.tablekok.store_service.domain.entity.Category;

import lombok.Builder;

@Builder
public record FindCategoryResult(
	UUID categoryId,
	String name
) {
	public static FindCategoryResult from(Category category) {
		return FindCategoryResult.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.build();
	}
}
