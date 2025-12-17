package com.tablekok.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.review_service.application.client.SearchClient;
import com.tablekok.review_service.application.client.dto.UpdateReviewStats;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchClientImpl implements SearchClient {

	private final SearchFeignClient searchFeignClient;

	@Override
	public void updateStoreStats(UUID storeId, UpdateReviewStats stats) {
		searchFeignClient.updateReviewStats(storeId, stats);
	}
}
