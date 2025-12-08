package com.tablekok.review_service.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetStoreReviewsResult(
	UUID reviewId,
	UUID storeId,
	UUID userId,
	Double rating,
	String content,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy
) {
}
