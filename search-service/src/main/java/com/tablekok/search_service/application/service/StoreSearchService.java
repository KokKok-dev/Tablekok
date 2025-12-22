package com.tablekok.search_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.cursor.dto.response.Cursor;
import com.tablekok.cursor.util.CursorUtils;
import com.tablekok.search_service.application.dto.result.SearchCategoryStoreResult;
import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.repository.StoreSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreSearchService {

	private final StoreSearchRepository storeSearchRepository;

	public Cursor<SearchCategoryStoreResult, String> searchByCategory(
		UUID categoryId,
		SortType sortType,
		CursorRequest<UUID> cursorRequest
	) {
		// 커서 값 파싱 (String -> 실제 타입)
		Object cursor = parseCursor(cursorRequest.cursor(), sortType);

		// Repository 호출 (limit = size + 1)
		List<StoreDocument> documents = storeSearchRepository.searchByCategory(
			categoryId.toString(),
			sortType,
			cursor,
			cursorRequest.cursorId() != null ? cursorRequest.cursorId().toString() : null,
			cursorRequest.getLimit()
		);

		List<SearchCategoryStoreResult> results = documents.stream()
			.map(SearchCategoryStoreResult::fromResult)
			.toList();

		// CursorResponse 생성 (다음 페이지 커서 추출)
		return CursorUtils.makeResponse(
			null,
			results,
			cursorRequest.size(),
			(result) -> extractNextCursor(result, sortType), // 다음 커서 값 추출
			SearchCategoryStoreResult::storeId// 다음 커서 ID 추출
		);
	}

	private Object parseCursor(String cursor, SortType sortType) {
		if (cursor == null) return null;
		return switch (sortType) {
			case RATING -> Double.parseDouble(cursor);
			case REVIEW -> Long.parseLong(cursor);
			case NAME -> cursor;
		};
	}

	private String extractNextCursor(SearchCategoryStoreResult result, SortType sortType) {
		return switch (sortType) {
			case RATING -> String.valueOf(result.averageRating());
			case REVIEW -> String.valueOf(result.reviewCount());
			case NAME -> result.name();
		};
	}
}
