package com.tablekok.user_service.auth.domain.exception;

import com.tablekok.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthDomainErrorCode implements ErrorCode {

	// 이메일 관련
	INVALID_EMAIL("AD001", "이메일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_EMAIL("AD002", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT),

	// 사업자번호 관련
	INVALID_BUSINESS_NUMBER_FORMAT("AD003", "사업자번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	INVALID_BUSINESS_NUMBER("AD004", "유효하지 않은 사업자번호입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_BUSINESS_NUMBER("AD005", "이미 등록된 사업자번호입니다.", HttpStatus.CONFLICT);

	private final String code;
	private final String message;
	private final HttpStatus status;  // httpStatus → status 로 변경
}
