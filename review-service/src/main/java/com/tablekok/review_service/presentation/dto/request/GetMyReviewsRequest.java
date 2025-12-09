package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.review_service.application.dto.command.GetMyReviewsCommand;

import jakarta.validation.constraints.PositiveOrZero;

public record GetMyReviewsRequest(
	String cursor,
	UUID cursorId,
	@PositiveOrZero
	Integer size
) {
	public GetMyReviewsCommand toCommand(UUID userId) {
		return new GetMyReviewsCommand(
			userId,
			cursor,
			cursorId,
			(size == null) ? 10 : size
		);
	}
}
