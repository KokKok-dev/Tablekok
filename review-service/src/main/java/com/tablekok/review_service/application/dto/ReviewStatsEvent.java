package com.tablekok.review_service.application.dto;

import java.util.UUID;

public record ReviewStatsEvent(
	UUID storeId,
	Double averageRating,
	Long reviewCount
) {
	public static ReviewStatsEvent of(UUID storeId, Double rating, Long count) {
		return new ReviewStatsEvent(storeId, rating, count);
	}
}
