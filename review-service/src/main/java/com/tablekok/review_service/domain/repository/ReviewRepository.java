package com.tablekok.review_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tablekok.review_service.application.dto.ReviewStatsDto;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;

public interface ReviewRepository {
	void save(Review review);

	Optional<Review> findById(UUID reviewId);

	boolean existsByReservationId(UUID reservationId);

	// 가게별 리뷰 목록 조회
	Page<Review> findReviewsByStoreId(
		UUID storeId,
		ReviewSortCriteria sortBy,
		String cursor,
		UUID cursorId,
		Pageable pageable);

	// 내가 작성한 리뷰 목록 조회
	Page<Review> findReviewsByUserId(
		UUID userId,
		String cursor,
		UUID cursorId,
		Pageable pageable);

	long countByStoreId(UUID storeId);

	long countByUserId(UUID userId);

	// 가게 리뷰수, 평균 평점 조회
	ReviewStatsDto getReviewStats(UUID storeId);
}
