package com.tablekok.store_service.application.dto.command;

import com.tablekok.store_service.domain.entity.Category;

import lombok.Builder;

@Builder
public record CreateCategoryCommand(
	String name
) {
	public Category toEntity() {
		return Category.of(name);
	}
}
