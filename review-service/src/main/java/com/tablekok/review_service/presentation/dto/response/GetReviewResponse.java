package com.tablekok.review_service.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.review_service.application.dto.result.GetReviewResult;

import lombok.Builder;

@Builder
public record GetReviewResponse(
	UUID reviewId,
	UUID userId,
	UUID storeId,
	UUID reservationId,
	Double rating,
	String content,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy
) {
	public static GetReviewResponse from(GetReviewResult result) {
		return GetReviewResponse.builder()
			.reviewId(result.reviewId())
			.userId(result.userId())
			.storeId(result.storeId())
			.reservationId(result.reservationId())
			.rating(result.rating())
			.content(result.content())
			.createdAt(result.createdAt())
			.createdBy(result.createdBy())
			.updatedAt(result.updatedAt())
			.updatedBy(result.updatedBy())
			.build();
	}
}
