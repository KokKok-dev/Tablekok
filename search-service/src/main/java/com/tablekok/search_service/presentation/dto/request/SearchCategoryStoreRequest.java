package com.tablekok.search_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.search_service.domain.document.SortType;

public record SearchCategoryStoreRequest(
	SortType sortBy,
	String cursor,
	UUID cursorId,
	Integer size
) {
	public SearchCategoryStoreRequest {
		if (sortBy == null) {
			sortBy = SortType.NAME;
		}
	}

	public CursorRequest<UUID> toCursorRequest() {
		return new CursorRequest<>(cursor, cursorId, size);
	}
}
