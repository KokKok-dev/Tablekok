package com.tablekok.user_service.user.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_TOKEN("U002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
	FORBIDDEN("U003", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	PASSWORD_NOT_MATCH("U004", "새 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
	INVALID_CURRENT_PASSWORD("U005", "현재 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
