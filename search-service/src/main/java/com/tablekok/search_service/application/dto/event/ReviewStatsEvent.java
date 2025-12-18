package com.tablekok.search_service.application.dto.event;

import java.util.UUID;

public record ReviewStatsEvent(
	UUID storeId,
	Double averageRating,
	Long reviewCount
) {
}
