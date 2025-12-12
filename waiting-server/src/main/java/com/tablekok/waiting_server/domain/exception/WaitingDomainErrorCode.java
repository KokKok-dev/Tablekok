package com.tablekok.waiting_server.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingDomainErrorCode implements ErrorCode {
	WAITING_CLOSED("WAITING104", "현재 매장이 웨이팅 접수 중이 아닙니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_BELOW_MIN("WAITING102", "최소 인원수보다 이상이여야 합니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_ABOVE_MAX("WAITING103", "최대 인원수보다 이하여야 합니다.", HttpStatus.BAD_REQUEST),
	;

	private final String code;
	private final String message;
	private final HttpStatus status;
}
