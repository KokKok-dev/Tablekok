package com.tablekok.review_service.application.dto.param;

import lombok.Builder;

@Builder
public record UpdateReviewParam(
	Double rating,
	String content
) {
}
