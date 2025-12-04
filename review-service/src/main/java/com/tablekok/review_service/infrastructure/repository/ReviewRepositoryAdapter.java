package com.tablekok.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tablekok.exception.AppException;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.exception.ReviewDomainErrorCode;
import com.tablekok.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;

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
}
