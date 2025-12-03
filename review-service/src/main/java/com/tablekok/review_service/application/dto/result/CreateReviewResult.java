package com.tablekok.review_service.application.dto.result;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

import lombok.Builder;

@Builder
public record CreateReviewResult(
	UUID reviewId,
	UUID userId,
	UUID storeId,
	UUID reservationId,
	Double rating,
	String content
) {
	public static CreateReviewResult fromEntity(Review review) {
		return CreateReviewResult.builder()
			.reviewId(review.getId())
			.userId(review.getUserId())
			.storeId(review.getStoreId())
			.reservationId(review.getReservationId())
			.rating(review.getRating())
			.content(review.getContent())
			.build();
	}
}
