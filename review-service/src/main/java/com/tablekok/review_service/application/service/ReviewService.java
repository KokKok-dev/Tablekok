package com.tablekok.review_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.review_service.application.client.ReservationClient;
import com.tablekok.review_service.application.dto.param.CreateReviewCommand;
import com.tablekok.review_service.application.dto.param.UpdateReviewCommand;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.dto.result.GetReviewResult;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.repository.ReviewRepository;
import com.tablekok.review_service.domain.service.ReviewDomainService;
import com.tablekok.review_service.domain.vo.Reservation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReservationClient reservationClient;
	private final ReviewDomainService reviewDomainService;

	@Transactional
	public CreateReviewResult createReview(CreateReviewCommand command, UUID userId) {
		// reservationClient로 storeId 가져와야함
		UUID storeId = UUID.randomUUID();

		// 이미 작성된 리뷰인지 검증
		reviewDomainService.validReview(command.reservationId());

		Reservation reservation = reservationClient.getReservation(
			command.reservationId()).toVo();

		// 예약 검증(예약 확인, 본인 확인, 예약 상태 확인)
		reviewDomainService.validReservation(reservation, userId);

		Review newReview = command.toEntity(userId, reservation.storeId());

		reviewRepository.save(newReview);

		return CreateReviewResult.fromEntity(newReview);
	}

	@Transactional
	public void updateReview(UUID reviewId, UpdateReviewCommand command) {
		Review foundReview = findReview(reviewId);
		foundReview.updateReview(command.rating(), command.content());
	}

	@Transactional
	public void deleteReview(UUID reviewId, UUID userId) {
		Review foundReview = findReview(reviewId);
		foundReview.softDelete(userId);
	}

	private Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId);
	}

	public GetReviewResult getReview(UUID reviewId) {
		Review foundReview = findReview(reviewId);
		return GetReviewResult.fromResult(foundReview);
	}
}
