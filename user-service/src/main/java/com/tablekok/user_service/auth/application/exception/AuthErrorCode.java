package com.tablekok.user_service.auth.application.exception;

import com.tablekok.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	// 인증 관련
	LOGIN_FAILED("A001", "이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
	USER_NOT_FOUND("A002", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_TOKEN("A003", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);

	private final String code;
	private final String message;
	private final HttpStatus status;  // httpStatus → status 로 변경
}
