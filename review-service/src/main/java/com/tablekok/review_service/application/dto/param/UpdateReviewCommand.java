package com.tablekok.review_service.application.dto.param;

import lombok.Builder;

@Builder
public record UpdateReviewCommand(
	Double rating,
	String content
) {
}
