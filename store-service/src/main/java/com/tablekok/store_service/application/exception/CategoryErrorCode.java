package com.tablekok.store_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
	DUPLICATE_CATEGORY_NAME("CATEGORY100", "카테고리 이름이 중복입니다.", HttpStatus.BAD_REQUEST),
	;

	private final String code;
	private final String message;
	private final HttpStatus status;

}
