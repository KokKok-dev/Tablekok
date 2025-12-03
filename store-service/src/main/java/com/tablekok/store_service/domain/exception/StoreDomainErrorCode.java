package com.tablekok.store_service.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum StoreDomainErrorCode implements ErrorCode {
	INVALID_CATEGORY_ID("STORE100", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_OPERATING_DAY("STORE101", "하나의 요일에 대해 두 개 이상의 영업 시간 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
	MISSING_ALL_OPERATING_DAYS("STORE102", "필수 요일 정보가 불완전합니다. 월요일부터 일요일까지 모든 요일 정보가 포함되어야 합니다.",
		HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

	StoreDomainErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

}
