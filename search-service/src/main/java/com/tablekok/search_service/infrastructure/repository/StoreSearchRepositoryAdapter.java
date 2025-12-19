package com.tablekok.search_service.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import com.tablekok.search_service.domain.document.SortType;
import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.repository.StoreSearchRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreSearchRepositoryAdapter implements StoreSearchRepository {

	private final StoreElasticSearchRepository storeElasticSearchRepository;
	private final ElasticsearchOperations elasticsearchOperations;
	private final StoreSearchQueryFactory queryFactory;

	@Override
	public void save(StoreDocument storeDocument) {
		storeElasticSearchRepository.save(storeDocument);
	}

	@Override
	public Optional<StoreDocument> findById(String storeId) {
		return storeElasticSearchRepository.findById(storeId);
	}

	@Override
	public List<StoreDocument> searchByCategory(
		String categoryId,
		SortType sortType,
		Object cursor,
		String cursorId,
		int limit
	) {
		Query query = queryFactory.createCategorySearchQuery(
			categoryId,
			sortType,
			cursor,
			cursorId,
			limit
		);

		SearchHits<StoreDocument> search = elasticsearchOperations.search(query, StoreDocument.class);

		return search.stream()
			.map(SearchHit::getContent)
			.toList();
	}
}
