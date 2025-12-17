package com.tablekok.reservation_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
	RESERVATION_NOT_FOUND(
		"RESERVATION100",
		"예약을 찾을 수 없습니다.",
		HttpStatus.NOT_FOUND),

	FORBIDDEN_ACCESS(
		"RESERVATION101",
		"접근 권한이 없습니다.",
		HttpStatus.NOT_FOUND),

	FORBIDDEN_STORE_ACCESS(
		"RESERVATION102",
		"해당 음식점에 대한 권한이 없습니다.",
		HttpStatus.FORBIDDEN),

	INTERNAL_CANNOT_CONNECT(
		"HOT-RESERVATION102",
		"내부 서비스 통신에 실패하였습니다. 다시 시도해주세요.",
		HttpStatus.SERVICE_UNAVAILABLE
	);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
