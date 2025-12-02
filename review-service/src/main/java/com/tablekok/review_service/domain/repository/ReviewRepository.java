package com.tablekok.review_service.domain.repository;

import java.util.UUID;

import com.tablekok.review_service.domain.entity.Review;

public interface ReviewRepository {
	Review save();
	Review findById(UUID reviewId);
	// Todo: 식당별 리뷰 목록 조회
	// Todo: 내가 작성한 리뷰 목록 조회
}
