package com.tablekok.review_service.domain.repository;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

public interface ReviewRepository {
	void save(Review review);
	Review findById(UUID reviewId);
	boolean existsByReservationId(UUID reservationId);
	// Todo: 식당별 리뷰 목록 조회
	// Todo: 내가 작성한 리뷰 목록 조회
}
