package com.tablekok.review_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

	REVIEW_NOT_FOUND(
		HttpStatus.NOT_FOUND,
		"REVIEW-100",
		"리뷰를 찾을 수 없습니다."
	),

	REVIEW_INVALID_USER(
		HttpStatus.FORBIDDEN,
		"REVIEW-101",
		"작성자만 리뷰를 수정, 삭제할 수 있습니다."
	),
	FORBIDDEN_ACCESS(
		HttpStatus.FORBIDDEN,
		"REVIEW-102",
		"접근 권한이 없습니다."
	)
	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
