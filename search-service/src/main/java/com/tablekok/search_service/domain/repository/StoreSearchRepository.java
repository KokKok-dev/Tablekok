package com.tablekok.search_service.domain.repository;

import java.util.Optional;

import com.tablekok.search_service.domain.document.StoreDocument;

public interface StoreSearchRepository {

	void save(StoreDocument storeDocument);

	Optional<StoreDocument> findById(String storeId);
}
