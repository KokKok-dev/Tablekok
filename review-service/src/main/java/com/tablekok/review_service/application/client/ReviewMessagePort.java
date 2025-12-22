package com.tablekok.review_service.application.client;

import java.util.UUID;

public interface ReviewMessagePort {

	void sendReviewStats(UUID storeId, Double averageRating, Long reviewCount);
}
