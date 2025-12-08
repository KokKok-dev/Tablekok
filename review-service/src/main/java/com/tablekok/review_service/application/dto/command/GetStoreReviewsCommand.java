package com.tablekok.review_service.application.dto.command;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

public record GetStoreReviewsCommand(
	UUID storeId,
	String cursor,
	UUID cursorId,
	int size,
	ReviewSortCriteria criteria
) {
}
