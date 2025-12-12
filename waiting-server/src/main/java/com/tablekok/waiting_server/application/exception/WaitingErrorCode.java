package com.tablekok.waiting_server.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingErrorCode implements ErrorCode {

	STORE_WAITING_STATUS_NOT_FOUND("WAITING100", "매장 웨이팅 상태 정보가 초기화되지 않았거나 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	WAITING_ALREADY_STARTED("WAITING101", "매장 웨이팅은 이미 시작되었습니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_BELOW_MIN("WAITING102", "최소 인원수보다 이상이여야 합니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_ABOVE_MAX("WAITING103", "최대 인원수보다 이하여야 합니다.", HttpStatus.BAD_REQUEST),
	WAITING_CLOSED("WAITING104", "현재 매장이 웨이팅 접수 중이 아닙니다.", HttpStatus.BAD_REQUEST),
	WAITING_ALREADY_CLOSED("WAITING105", "이미 웨이팅 접수가 중단된 상태입니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
