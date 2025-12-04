package com.tablekok.review_service.presentation.dto.request;

import com.tablekok.review_service.application.dto.param.UpdateReviewParam;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateReviewRequest(
	@Min(1)
	@Max(5)
	Double rating,
	String content
) {
	public UpdateReviewParam toParam() {
		return UpdateReviewParam.builder()
			.rating(rating)
			.content(content)
			.build();
	}
}
