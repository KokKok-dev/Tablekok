package com.tablekok.review_service.application.dto.command;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

import lombok.Builder;

@Builder
public record CreateReviewCommand(
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
