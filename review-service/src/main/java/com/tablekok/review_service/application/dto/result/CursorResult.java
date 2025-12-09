package com.tablekok.review_service.application.dto.result;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record CursorResult<T>(
	List<T> contents,
	UUID nextCursorId,
	String nextCursor,
	boolean hasNext
) {
	public <R> CursorResult<R> map(Function<T, R> mapper) {
		List<R> mappedContent = contents.stream().map(mapper).toList();
		return new CursorResult<>(mappedContent, nextCursorId, nextCursor, hasNext);
	}
}
