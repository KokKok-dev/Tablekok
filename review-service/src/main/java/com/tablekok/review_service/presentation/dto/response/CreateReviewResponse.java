package com.tablekok.review_service.presentation.dto.response;

import java.util.UUID;

import com.tablekok.review_service.application.dto.result.CreateReviewResult;

import lombok.Builder;

@Builder
public record CreateReviewResponse(
	UUID reviewId,
	UUID userId,
	UUID storeId,
	UUID reservationId,
	Double rating,
	String content
) {
	public static CreateReviewResponse from(CreateReviewResult result) {
		return CreateReviewResponse.builder()
			.reviewId(result.reviewId())
			.userId(result.userId())
			.storeId(result.storeId())
			.reservationId(result.reservationId())
			.rating(result.rating())
			.content(result.content())
			.build();
	}
}
