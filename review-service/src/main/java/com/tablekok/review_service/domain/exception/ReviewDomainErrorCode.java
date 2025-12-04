package com.tablekok.review_service.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewDomainErrorCode implements ErrorCode {

	REVIEW_ALREADY_EXISTS(
		HttpStatus.CONFLICT,
		"REVIEW-001",
		"이미 해당 예약에 대한 리뷰가 존재합니다."
	),
	RESERVATION_NOT_FOUND(
		HttpStatus.NOT_FOUND,
		"REVIEW-002",
		"예약 정보를 찾을 수 없습니다."
	),
	RESERVATION_NOT_VISITED(
		HttpStatus.BAD_REQUEST,
		"REVIEW-003",
		"방문 완료된 예약만 리뷰를 작성할 수 있습니다."
	),
	REVIEW_PERMISSION_DENIED(
		HttpStatus.FORBIDDEN,
		"REVIEW-004",
		"본인의 예약에 대해서만 리뷰를 작성할 수 있습니다."
	),
	REVIEW_NOT_FOUND(
		HttpStatus.NOT_FOUND,
		"REVIEW-005",
		"리부를 찾을 수 없습니다."
	);

	private final HttpStatus status;
	private final String code;
	private final String message;
}
