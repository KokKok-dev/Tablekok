package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.cursor.dto.request.CursorRequest;

public record GetMyReviewCursorRequest(
	String cursor,
	UUID cursorId,
	Integer size
) {
	public CursorRequest<UUID> toCursorRequest() {
		return new CursorRequest<>(cursor, cursorId, size);
	}
}
