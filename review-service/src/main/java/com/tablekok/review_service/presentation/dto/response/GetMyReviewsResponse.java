package com.tablekok.review_service.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetMyReviewsResponse(
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
