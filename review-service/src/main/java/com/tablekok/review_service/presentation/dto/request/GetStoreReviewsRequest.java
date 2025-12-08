package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.review_service.application.dto.command.GetStoreReviewsCommand;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

import jakarta.validation.constraints.PositiveOrZero;

public record GetStoreReviewsRequest(
	String cursor,
	UUID cursorId,
	@PositiveOrZero
	Integer size,
	ReviewSortCriteria sortBy
) {
	public GetStoreReviewsCommand toCommand(UUID storeId) {
		return new GetStoreReviewsCommand(
			storeId,
			cursor,
			cursorId,
			(size == null) ? 10 : size,
			(sortBy == null) ? ReviewSortCriteria.NEWEST : sortBy
		);
	}
}
