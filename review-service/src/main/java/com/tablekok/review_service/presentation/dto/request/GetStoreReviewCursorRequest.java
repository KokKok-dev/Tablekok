package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

public record GetStoreReviewCursorRequest(
	String cursor,
	UUID cursorId,
	Integer size,
	ReviewSortCriteria sortBy
) {
	public GetStoreReviewCursorRequest {
		if (sortBy == null) {
			sortBy = ReviewSortCriteria.NEWEST;
		}
	}

	public CursorRequest<UUID> toCursorRequest() {
		return new CursorRequest<>(cursor, cursorId, size);
	}
}
