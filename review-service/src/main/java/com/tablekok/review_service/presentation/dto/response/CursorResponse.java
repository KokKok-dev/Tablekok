package com.tablekok.review_service.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com.tablekok.review_service.application.dto.result.CursorResult;

public record CursorResponse<T>(
	List<T> contents,
	UUID netCursorId,
	String nextCursor,
	boolean hasNext
) {
	public static <T> CursorResponse<T> from(CursorResult<T> result) {
		return new CursorResponse<T>(
			result.contents(),
			result.nextCursorId(),
			result.nextCursor(),
			result.hasNext()
		);
	}
}
