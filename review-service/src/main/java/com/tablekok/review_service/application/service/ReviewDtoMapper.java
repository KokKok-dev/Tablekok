package com.tablekok.review_service.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.tablekok.review_service.application.dto.result.CursorResult;
import com.tablekok.review_service.application.dto.result.GetMyReviewsResult;
import com.tablekok.review_service.application.dto.result.GetStoreReviewsResult;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewDtoMapper {

	private final CursorResultFactory cursorResultFactory;

	public CursorResult<GetStoreReviewsResult> toStoreReviewsCursorResult(
		Page<Review> page,
		int size,
		ReviewSortCriteria sortBy
	) {
		return cursorResultFactory.create(
			page, size, sortBy,
			review -> new GetStoreReviewsResult(
				review.getId(),
				review.getStoreId(),
				review.getUserId(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt(),
				review.getCreatedBy(),
				review.getUpdatedAt(),
				review.getUpdatedBy()
			)
		);
	}

	public CursorResult<GetMyReviewsResult> toMyReviewsCursorResult(
		Page<Review> page,
		int size,
		ReviewSortCriteria sortBy
	) {
		return cursorResultFactory.create(
			page, size, sortBy,
			review -> new GetMyReviewsResult(
				review.getId(),
				review.getStoreId(),
				review.getUserId(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt(),
				review.getCreatedBy(),
				review.getUpdatedAt(),
				review.getUpdatedBy()
			)
		);
	}
}
