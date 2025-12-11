package com.tablekok.review_service.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

import lombok.Builder;

@Builder
public record GetMyReviewsResult(
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
	public static GetMyReviewsResult from(Review review) {
		return GetMyReviewsResult.builder()
			.reviewId(review.getId())
			.storeId(review.getStoreId())
			.userId(review.getUserId())
			.rating(review.getRating())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.createdBy(review.getCreatedBy())
			.updatedAt(review.getUpdatedAt())
			.updatedBy(review.getUpdatedBy())
			.build();
	}
}
