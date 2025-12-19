package com.tablekok.review_service.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.review_service.application.dto.result.GetMyReviewsResult;

import lombok.Builder;

@Builder
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
	public static GetMyReviewsResponse from(GetMyReviewsResult result) {
		return GetMyReviewsResponse.builder()
			.reviewId(result.reviewId())
			.storeId(result.storeId())
			.userId(result.userId())
			.rating(result.rating())
			.content(result.content())
			.createdAt(result.createdAt())
			.createdBy(result.createdBy())
			.updatedAt(result.updatedAt())
			.updatedBy(result.updatedBy())
			.build();
	}
}
