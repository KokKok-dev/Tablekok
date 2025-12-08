package com.tablekok.review_service.application.dto.command;

import java.util.UUID;

public record GetMyReviewsCommand(
	UUID userId,
	String cursor,
	UUID cursorId,
	int size
) {
}
