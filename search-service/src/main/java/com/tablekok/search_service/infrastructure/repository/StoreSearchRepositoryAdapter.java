package com.tablekok.search_service.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.repository.StoreSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreSearchRepositoryAdapter implements StoreSearchRepository {

	private final StoreElasticSearchRepository storeElasticSearchRepository;

	@Override
	public void save(StoreDocument storeDocument) {
		storeElasticSearchRepository.save(storeDocument);
	}

	@Override
	public Optional<StoreDocument> findById(String storeId) {
		return storeElasticSearchRepository.findById(storeId);
	}
}
