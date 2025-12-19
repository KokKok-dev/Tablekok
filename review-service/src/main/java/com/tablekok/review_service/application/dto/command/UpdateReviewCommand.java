package com.tablekok.review_service.application.dto.command;

import lombok.Builder;

@Builder
public record UpdateReviewCommand(
	Double rating,
	String content
) {
}
