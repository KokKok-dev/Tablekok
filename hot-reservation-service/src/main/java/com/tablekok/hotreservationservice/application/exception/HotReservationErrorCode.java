package com.tablekok.hotreservationservice.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HotReservationErrorCode implements ErrorCode {
	RESERVATION_TOKEN_VALIDATION_FAILED(
		"HOT-RESERVATION100",
		"유효하지 않은 입장 토큰입니다.",
		HttpStatus.BAD_REQUEST
	);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
