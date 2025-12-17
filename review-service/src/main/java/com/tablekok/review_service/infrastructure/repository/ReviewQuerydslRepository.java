package com.tablekok.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tablekok.review_service.application.dto.ReviewStatsDto;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

public interface ReviewQuerydslRepository {

	Page<Review> findReviewsByStoreId(
		UUID storeId,
		ReviewSortCriteria sortBy,
		String cursor,
		UUID cursorId,
		Pageable pageable
	);

	Page<Review> findReviewsByUserId(
		UUID userId,
		String cursor,
		UUID cursorId,
		Pageable pageable
	);

	ReviewStatsDto getReviewStats(UUID storeId);
}
