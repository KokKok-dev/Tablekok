package com.tablekok.review_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.review_service.application.client.ReservationClient;
import com.tablekok.review_service.application.dto.command.CreateReviewCommand;
import com.tablekok.review_service.application.dto.command.GetMyReviewsCommand;
import com.tablekok.review_service.application.dto.command.GetStoreReviewsCommand;
import com.tablekok.review_service.application.dto.command.UpdateReviewCommand;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.dto.result.CursorResult;
import com.tablekok.review_service.application.dto.result.GetMyReviewsResult;
import com.tablekok.review_service.application.dto.result.GetReviewResult;
import com.tablekok.review_service.application.dto.result.GetStoreReviewsResult;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;
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
	private final ReviewDtoMapper reviewDtoMapper;

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

	public CursorResult<GetStoreReviewsResult> findStoreReviews(GetStoreReviewsCommand command) {
		Pageable pageable = PageRequest.of(0, command.size() + 1);

		Page<Review> storeReviews = reviewRepository.findReviewsByStoreId(
			command.storeId(),
			command.sortBy(),
			command.cursor(),
			command.cursorId(),
			pageable
		);

		return reviewDtoMapper.toStoreReviewsCursorResult(storeReviews, command.size(), command.sortBy());
	}

	public CursorResult<GetMyReviewsResult> findMyReviews(GetMyReviewsCommand command) {
		Pageable pageable = PageRequest.of(0, command.size() + 1);

		Page<Review> myReviews = reviewRepository.findReviewsByUserId(
			command.userId(),
			command.cursor(),
			command.cursorId(),
			pageable
		);

		return reviewDtoMapper.toMyReviewsCursorResult(myReviews, command.size(), ReviewSortCriteria.NEWEST);
	}
}
