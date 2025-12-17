package com.tablekok.review_service.application.client.dto;

import lombok.Builder;

@Builder
public record UpdateReviewStats(
	Double averageRating,
	Long reviewCount
) {
}
