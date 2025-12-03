package com.tablekok.review_service.application.dto.param;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

import lombok.Builder;

@Builder
public record CreateReviewParam(
	UUID reservationId,
	double rating,
	String content
) {
	public Review toEntity(UUID userId, UUID storeId) {
		return Review.create(
			userId,
			storeId,
			reservationId,
			rating,
			content
		);
	}
}
