package com.tablekok.search_service.domain.vo;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.search_service.domain.document.SortType;

public record StoreSearchCriteria(
	String keyword,
	SortType sortType,
	CursorRequest<String> cursorRequest
) {
}
