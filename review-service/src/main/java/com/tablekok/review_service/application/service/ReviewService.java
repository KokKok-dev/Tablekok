package com.tablekok.review_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.cursor.dto.request.CursorRequest;
import com.tablekok.cursor.dto.response.Cursor;
import com.tablekok.cursor.util.CursorUtils;
import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.review_service.application.client.ReservationClient;
import com.tablekok.review_service.application.client.ReviewMessagePort;
import com.tablekok.review_service.application.client.SearchClient;
import com.tablekok.review_service.application.client.dto.UpdateReviewStats;
import com.tablekok.review_service.application.dto.ReviewStatsDto;
import com.tablekok.review_service.application.dto.command.CreateReviewCommand;
import com.tablekok.review_service.application.dto.command.UpdateReviewCommand;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.dto.result.GetMyReviewsResult;
import com.tablekok.review_service.application.dto.result.GetReviewResult;
import com.tablekok.review_service.application.dto.result.GetStoreReviewsResult;
import com.tablekok.review_service.application.exception.ReviewErrorCode;
import com.tablekok.review_service.domain.entity.Review;
import com.tablekok.review_service.domain.entity.ReviewSortCriteria;
import com.tablekok.review_service.domain.repository.ReviewRepository;
import com.tablekok.review_service.domain.service.ReviewDomainService;
import com.tablekok.review_service.domain.vo.Reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReservationClient reservationClient;
	private final ReviewDomainService reviewDomainService;
	private final SearchClient searchClient;
	private final ReviewMessagePort reviewMessagePort;

	@Transactional
	public CreateReviewResult createReview(CreateReviewCommand command, UUID userId) {
		// 이미 작성된 리뷰인지 검증
		reviewDomainService.validReview(command.reservationId());

		Reservation reservation = reservationClient.getReservation(
			command.reservationId()).toVo();

		// 예약 검증(예약 확인, 본인 확인, 예약 상태 확인)
		reviewDomainService.validReservation(reservation, userId);

		Review newReview = command.toEntity(userId, reservation.storeId());

		reviewRepository.save(newReview);

		syncStoreStats(reservation.storeId());

		return CreateReviewResult.fromEntity(newReview);
	}

	@Transactional
	public void updateReview(UUID reviewId, UUID userId, UpdateReviewCommand command) {
		Review foundReview = findReview(reviewId);

		validateReviewAuthor(foundReview, userId);

		foundReview.updateReview(command.rating(), command.content());

		syncStoreStats(foundReview.getStoreId());
	}

	@Transactional
	public void deleteReview(UUID reviewId, UUID userId, String role) {
		Review foundReview = findReview(reviewId);

		if (!role.equals(UserRole.MASTER)) {
			validateReviewAuthor(foundReview, userId);
		}

		foundReview.softDelete(userId);

		syncStoreStats(foundReview.getStoreId());
	}

	public GetReviewResult getReview(UUID reviewId) {
		Review foundReview = reviewRepository.findById(reviewId).orElseThrow(
			() -> new AppException(ReviewErrorCode.REVIEW_NOT_FOUND));
		return GetReviewResult.fromResult(foundReview);
	}

	public Cursor<GetStoreReviewsResult, UUID> findStoreReviews(
		UUID storeId,
		CursorRequest<UUID> request,
		ReviewSortCriteria sortBy
	) {
		Pageable pageable = PageRequest.of(0, request.getLimit());

		Page<Review> storeReviews = reviewRepository.findReviewsByStoreId(
			storeId, sortBy, request.cursor(), request.cursorId(), pageable);

		long totalCount = reviewRepository.countByStoreId(storeId);

		Cursor<Review, UUID> response = CursorUtils.makeResponse(
			totalCount,
			storeReviews.getContent(),
			request.size(),
			review -> {
				if (sortBy == ReviewSortCriteria.RATING_HIGH || sortBy == ReviewSortCriteria.RATING_LOW) {
					return String.valueOf(review.getRating());
				}
				return review.getCreatedAt().toString();
			},
			Review::getId
		);

		return response.map(GetStoreReviewsResult::from);
	}

	public Cursor<GetMyReviewsResult, UUID> findMyReviews(UUID userId, CursorRequest<UUID> request) {
		Pageable pageable = PageRequest.of(0, request.getLimit());

		Page<Review> myReviews = reviewRepository.findReviewsByUserId(
			userId, request.cursor(), request.cursorId(), pageable);

		long totalCount = reviewRepository.countByUserId(userId);

		Cursor<Review, UUID> response = CursorUtils.makeResponse(
			totalCount,
			myReviews.getContent(),
			request.size(),
			review -> review.getCreatedAt().toString(),
			Review::getId
		);

		return response.map(GetMyReviewsResult::from);
	}

	private Review findReview(UUID reviewId) {
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new AppException(ReviewErrorCode.REVIEW_NOT_FOUND));
	}

	// 리뷰 수정, 삭제 시 사용자 검증
	private void validateReviewAuthor(Review review, UUID userId) {
		if (!review.getUserId().equals(userId)) {
			throw new AppException(ReviewErrorCode.REVIEW_INVALID_USER);
		}
	}

	// 가게 리뷰수, 평균 평점 -> search-service로 전달
	private void syncStoreStats(UUID storeId) {
		ReviewStatsDto stats = reviewRepository.getReviewStats(storeId);
		reviewMessagePort.sendReviewStats(
			storeId, stats.averageRating(), stats.reviewCount());
	}
}
