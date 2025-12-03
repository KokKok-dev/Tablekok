package com.tablekok.store_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;

@Getter
public enum StoreErrorCode implements ErrorCode {

	DUPLICATE_STORE_ENTRY("STORE100", "중복된 음식점입니다.", HttpStatus.BAD_REQUEST),
	INVALID_CATEGORY_ID("STORE101", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

	StoreErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
