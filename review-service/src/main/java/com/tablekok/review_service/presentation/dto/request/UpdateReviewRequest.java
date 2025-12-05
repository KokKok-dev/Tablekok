package com.tablekok.review_service.presentation.dto.request;

import com.tablekok.review_service.application.dto.param.UpdateReviewCommand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
	@Min(1)
	@Max(5)
	Double rating,
	@Size(min = 10, max = 1000, message = "리뷰는 10자 이상, 1000자 이하로 작성해야합니다.")
	String content
) {
	public UpdateReviewCommand toCommand() {
		return UpdateReviewCommand.builder()
			.rating(rating)
			.content(content)
			.build();
	}
}
