package com.tablekok.review_service.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.review_service.domain.exception.ReviewDomainErrorCode;
import com.tablekok.review_service.domain.repository.ReviewRepository;
import com.tablekok.review_service.domain.vo.Reservation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewDomainService {
	private final ReviewRepository reviewRepository;

	@Transactional(readOnly = true)
	public void validReview(UUID reservationId) {
		// 이미 리뷰를 작성한 예약인지 확인
		if (reviewRepository.existsByReservationId(reservationId)) {
			throw new AppException(ReviewDomainErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}

	public void validReservation(Reservation reservation, UUID userId) {
		// 예약 정보가 없는 경우
		if (reservation == null) {
			throw new AppException(ReviewDomainErrorCode.RESERVATION_NOT_FOUND);
		}

		// 요청한 유저와 예약자가 일치하는지 검증
		if (!reservation.userId().equals(userId)) {
			throw new AppException(ReviewDomainErrorCode.REVIEW_PERMISSION_DENIED);
		}

		// 방문 완료 상태인지 확인
		if (!reservation.reservationStatus().equalsIgnoreCase("DONE")) {
			throw new AppException(ReviewDomainErrorCode.RESERVATION_NOT_VISITED);
		}
	}
}
