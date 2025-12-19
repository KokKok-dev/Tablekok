package com.tablekok.review_service.application.client;

import java.util.UUID;

import com.tablekok.review_service.application.client.dto.UpdateReviewStats;

public interface SearchClient {

	void updateStoreStats(UUID storeId, UpdateReviewStats stats);
}
