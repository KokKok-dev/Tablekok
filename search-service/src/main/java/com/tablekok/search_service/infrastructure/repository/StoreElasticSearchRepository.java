package com.tablekok.search_service.infrastructure.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.tablekok.search_service.domain.document.StoreDocument;

public interface StoreElasticSearchRepository extends ElasticsearchRepository<StoreDocument, String> {
}
