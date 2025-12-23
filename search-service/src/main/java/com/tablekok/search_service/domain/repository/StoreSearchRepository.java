package com.tablekok.search_service.domain.repository;

import java.util.List;
import java.util.Optional;

import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.vo.StoreSearchCriteria;

public interface StoreSearchRepository {

	void save(StoreDocument storeDocument);

	Optional<StoreDocument> findById(String storeId);

	List<StoreDocument> searchByCategory(
		String categoryId,
		SortType sortType,
		Object cursor,
		String cursorId,
		int limit
	);

	// 검색
	List<StoreDocument> search(StoreSearchCriteria criteria);
}
