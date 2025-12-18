package com.tablekok.waiting_server.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingErrorCode implements ErrorCode {

	STORE_WAITING_STATUS_NOT_FOUND("WAITING100", "매장 웨이팅 상태 정보가 초기화되지 않았거나 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	WAITING_NOT_FOUND("WAITING101", "해당 웨이팅은 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
	INVALID_WAITING_STATUS("WAITING102", "웨이팅 상태가 유효하지 않아 요청을 처리할 수 없습니다.", HttpStatus.BAD_REQUEST),
	CONNECT_FORBIDDEN("WAITING103", "본인의 웨이팅만 연결할 수 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_CUSTOMER_TYPE("WAITING104", "정의되지 않은 고객 타입입니다.", HttpStatus.BAD_REQUEST),
	WAITING_NOT_IN_STORE("WAITING105", "매장에 해당 waitingId가 없습니다.", HttpStatus.BAD_REQUEST),
	FORBIDDEN_ACCESS("WAITING106", "접근권한이 없습니다.", HttpStatus.FORBIDDEN),
	NOT_STORE_OWNER("WAITING107", "해당 매장의 소유주가 아닙니다.", HttpStatus.FORBIDDEN),
	STORE_NOT_FOUND("WATING108", "해당 매장을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
	private final String code;
	private final String message;
	private final HttpStatus status;
}
