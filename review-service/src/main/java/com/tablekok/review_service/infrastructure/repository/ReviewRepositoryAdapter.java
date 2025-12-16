package com.tablekok.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.tablekok.exception.AppException;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;
import com.tablekok.review_service.domain.exception.ReviewDomainErrorCode;
import com.tablekok.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;
	private final ReviewQuerydslRepository reviewQuerydslRepository;

	@Override
	public void save(Review review) {
		reviewJpaRepository.save(review);
	}

	@Override
	public Review findById(UUID reviewId) {
		return reviewJpaRepository.findById(reviewId).orElseThrow(
			() -> new AppException(ReviewDomainErrorCode.REVIEW_NOT_FOUND));
	}

	@Override
	public boolean existsByReservationId(UUID reservationId) {
		return reviewJpaRepository.existsByReservationId(reservationId);
	}

	@Override
	public Page<Review> findReviewsByStoreId(
		UUID storeId,
		ReviewSortCriteria sortBy,
		String cursor,
		UUID cursorId,
		Pageable pageable
	) {
		return reviewQuerydslRepository.findReviewsByStoreId(storeId, sortBy, cursor, cursorId, pageable);
	}

	@Override
	public Page<Review> findReviewsByUserId(
		UUID userId,
		String cursor,
		UUID cursorId,
		Pageable pageable
	) {
		return reviewQuerydslRepository.findReviewsByUserId(userId, cursor, cursorId, pageable);
	}

	@Override
	public long countByStoreId(UUID storeId) {
		return reviewJpaRepository.countByStoreId(storeId);
	}

	@Override
	public long countByUserId(UUID userId) {
		return reviewJpaRepository.countByUserId(userId);
	}
}
