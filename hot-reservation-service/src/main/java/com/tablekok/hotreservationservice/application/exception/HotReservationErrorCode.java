package com.tablekok.hotreservationservice.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HotReservationErrorCode implements ErrorCode {
	AVAILABLE_USER_VALIDATION_FAILED(
		"HOT-RESERVATION100",
		"예약이 허용되지 않은 유저입니다.",
		HttpStatus.BAD_REQUEST
	),

	COMMUNICATOR_SEND_FAILED(
		"HOT-RESERVATION101",
		"이벤트 송신에 실패하였습니다. 재연결을 시도하세요.",
		HttpStatus.INTERNAL_SERVER_ERROR
	),

	INTERNAL_CANNOT_CONNECT(
		"HOT-RESERVATION102",
		"내부 서비스 통신에 실패하였습니다. 다시 시도해주세요.",
		HttpStatus.SERVICE_UNAVAILABLE
	),

	FORBIDDEN_ACCESS(
		"HOT-RESERVATION103",
		"접근권한이 없습니다.",
		HttpStatus.FORBIDDEN
	);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
