package com.tablekok.store_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {

	// ----------------------------------------------------
	// STORE1xx: 음식점 관련 오류
	// ----------------------------------------------------
	DUPLICATE_STORE_ENTRY("STORE100", "중복된 음식점입니다.", HttpStatus.BAD_REQUEST),
	STORE_NOT_FOUND("STORE101", "음식점을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	// ----------------------------------------------------
	// RP1xx: 예약 정책 (ReservationPolicy) 검증 오류
	// ----------------------------------------------------
	POLICY_ALREADY_EXISTS("RP100", "해당 음식점의 예약 정책이 이미 등록되어 있습니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
