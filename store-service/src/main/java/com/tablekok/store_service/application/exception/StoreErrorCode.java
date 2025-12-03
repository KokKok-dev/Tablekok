package com.tablekok.store_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum StoreErrorCode implements ErrorCode {

	DUPLICATE_STORE_ENTRY("STORE100", "중복된 음식점입니다.", HttpStatus.BAD_REQUEST),
	INVALID_CATEGORY_ID("STORE101", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST),

	// 운영 시간 검증 관련 오류 코드
	INVALID_CLOSED_TIME("STORE102", "휴무일(isClosed=true) 설정 시 영업 시간(openTime, closeTime)을 지정할 수 없습니다.",
		HttpStatus.BAD_REQUEST),
	MISSING_OPERATING_TIME("STORE103", "영업일(isClosed=false) 설정 시 시작 시간과 종료 시간은 필수입니다.", HttpStatus.BAD_REQUEST),
	INVALID_TIME_RANGE("STORE104", "영업 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_OPERATING_DAY("STORE105", "하나의 요일에 대해 두 개 이상의 영업 시간 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
	MISSING_ALL_OPERATING_DAYS("STORE106", "필수 요일 정보가 불완전합니다. 월요일부터 일요일까지 모든 요일 정보가 포함되어야 합니다.",
		HttpStatus.BAD_REQUEST),
	;

	private final String code;
	private final String message;
	private final HttpStatus status;

	StoreErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
