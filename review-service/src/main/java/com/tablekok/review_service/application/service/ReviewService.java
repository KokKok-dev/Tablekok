package com.tablekok.review_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.review_service.application.client.ReservationClient;
import com.tablekok.review_service.application.dto.param.CreateReviewParam;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
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
	public CreateReviewResult createReview(CreateReviewParam param, UUID userId) {
		// reservationClient로 storeId 가져와야함
		UUID storeId = UUID.randomUUID();

		// 이미 작성된 리뷰인지 검증
		reviewDomainService.validReview(param.reservationId());

		Reservation reservation = reservationClient.getReservation(
			param.reservationId()).toVo();

		// 예약 검증(예약 확인, 본인 확인, 예약 상태 확인)
		reviewDomainService.validReservation(reservation, userId);

		Review newReview = param.toEntity(userId, reservation.storeId());

		reviewRepository.save(newReview);

		return CreateReviewResult.fromEntity(newReview);
	}
}
