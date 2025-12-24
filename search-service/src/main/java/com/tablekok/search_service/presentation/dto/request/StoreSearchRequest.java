package com.tablekok.search_service.presentation.dto.request;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.search_service.application.dto.command.StoreSearchCommand;
import com.tablekok.search_service.domain.document.SortType;

public record StoreSearchRequest(
	String keyword,
	SortType sortType,
	String cursor,
	String cursorId,
	Integer limit
) {
	public StoreSearchRequest {
		if (sortType == null) {
			sortType = SortType.RATING;
		}
		if (limit == null) {
			limit = 10;
		}
	}

	public StoreSearchCommand toCommand() {
		CursorRequest<String> cursorRequest = new CursorRequest<>(
			this.cursor,
			this.cursorId,
			this.limit
		);

		return new StoreSearchCommand(keyword, sortType, cursorRequest);
	}
}
