package com.tablekok.store_service.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreDomainErrorCode implements ErrorCode {
	INVALID_CATEGORY_ID("STORE000", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_OPERATING_DAY("STORE001", "하나의 요일에 대해 두 개 이상의 영업 시간 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
	MISSING_ALL_OPERATING_DAYS("STORE002", "필수 요일 정보가 불완전합니다. 월요일부터 일요일까지 모든 요일 정보가 포함되어야 합니다.",
		HttpStatus.BAD_REQUEST),
	INVALID_CLOSED_TIME("STORE003", "휴무일(isClosed=true) 설정 시 영업 시간(openTime, closeTime)을 지정할 수 없습니다.",
		HttpStatus.BAD_REQUEST),
	MISSING_OPERATING_TIME("STORE004", "영업일(isClosed=false) 설정 시 시작 시간과 종료 시간은 필수입니다.", HttpStatus.BAD_REQUEST),
	INVALID_TIME_RANGE("STORE005", "영업 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
