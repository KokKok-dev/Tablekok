package com.tablekok.search_service.application.dto.command;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.vo.StoreSearchCriteria;

public record StoreSearchCommand(
	String keyword,
	SortType sortType,
	CursorRequest<String> cursorRequest
) {
	public StoreSearchCriteria toDomainCriteria() {
		return new StoreSearchCriteria(
			keyword,
			sortType,
			cursorRequest
		);
	}
}
