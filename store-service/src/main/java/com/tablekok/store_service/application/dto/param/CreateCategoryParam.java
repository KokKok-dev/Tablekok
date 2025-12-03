package com.tablekok.store_service.application.dto.param;

import com.tablekok.store_service.domain.entity.Category;

import lombok.Builder;

@Builder
public record CreateCategoryParam(
	String name
) {
	public Category toEntity() {
		return Category.of(name);
	}
}
