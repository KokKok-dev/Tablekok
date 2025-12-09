package com.tablekok.user_service.auth.application.exception;

import com.tablekok.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Auth Application 계층 전용 ErrorCode
 * ✅ gashine20 피드백 반영: Application 계층용 별도 ErrorCode
 *
 * Application 계층에서 발생하는 비즈니스 로직 에러 정의
 * Common의 GlobalExceptionHandler에서 자동으로 처리됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	// ========== 인증 관련 에러 (401) ==========

	INVALID_CREDENTIALS("A001", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
	LOGIN_FAILED("A002", "로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED),

	// ========== 계정 상태 관련 에러 (403) ==========

	ACCOUNT_DEACTIVATED("A100", "비활성화된 계정입니다. 관리자에게 문의하세요.", HttpStatus.FORBIDDEN),
	ACCOUNT_LOCKED("A101", "계정이 잠겼습니다. 관리자에게 문의하세요.", HttpStatus.FORBIDDEN),

	// ========== 사용자 조회 관련 에러 (404) ==========

	USER_NOT_FOUND("A200", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	EMAIL_NOT_FOUND("A201", "가입되지 않은 이메일입니다.", HttpStatus.NOT_FOUND),

	// ========== 토큰 관련 에러 (401) ==========

	TOKEN_EXPIRED("A300", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	TOKEN_INVALID("A301", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),

	// ========== 내부 서버 에러 (5XX) ==========

	SIGNUP_FAILED("A500", "회원가입 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	PASSWORD_ENCODING_ERROR("A501", "비밀번호 암호화 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
