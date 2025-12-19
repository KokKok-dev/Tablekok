package com.tablekok.review_service.application.dto.result;

import java.time.LocalDateTime;
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
	String content,
	LocalDateTime createdAt,
	UUID createdBy
) {
	public static CreateReviewResult fromEntity(Review review) {
		return CreateReviewResult.builder()
			.reviewId(review.getId())
			.userId(review.getUserId())
			.storeId(review.getStoreId())
			.reservationId(review.getReservationId())
			.rating(review.getRating())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.createdBy(review.getCreatedBy())
			.build();
	}
}
