package com.tablekok.store_service.presentation.dto.request;

import com.tablekok.store_service.application.dto.param.CreateCategoryParam;

import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
	@NotNull(message = "카테고리 이름은 필수입니다.")
	String name

) {
	public CreateCategoryParam toParam() {
		return CreateCategoryParam.builder()
			.name(name)
			.build();
	}
}
