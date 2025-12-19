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
	DUPLICATE_STORE_INFO("STORE100", "중복된 음식점입니다.", HttpStatus.BAD_REQUEST),
	STORE_NOT_FOUND("STORE101", "음식점을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

	MASTER_INVALID_STATUS_TRANSITION("STORE102",
		"마스터 권한은 승인(PENDING)/거부(REJECTED), 운영(OPERATING), 폐점(DECOMMISSIONED) 상태로만 전환 가능합니다. 일시적 운영 상태는 Owner 권한으로 처리해 주세요.",
		HttpStatus.BAD_REQUEST),
	OWNER_FORBIDDEN_CURRENT_STATUS_TRANSITION("STORE103", "현재 상태에서 OWNER는 상태를 변경할 권한이 없습니다.", HttpStatus.FORBIDDEN),
	OWNER_FORBIDDEN_STATUS_TRANSITION("STORE104", "OWNER는 승인 관련 상태를 변경할 권한이 없습니다.", HttpStatus.BAD_REQUEST),
	UNSUPPORTED_USER_ROLE("STORE105", "현재 역할에 대한 상태 전환 전략이 정의되지 않았습니다.", HttpStatus.BAD_REQUEST),
	MASTER_FORBIDDEN_REVERSION_TRANSITION("STORE106", "이미 승인된 상태에서 PENDING_APPROVAL로의 역전환은 안됩니다.",
		HttpStatus.BAD_REQUEST),

	FORBIDDEN_ACCESS("STORE107", "접근권한이 없습니다.", HttpStatus.BAD_REQUEST),
	OPERATING_HOUR_NOT_FOUND("STORE108", "해당 운영시간이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
	STORE_CLOSED_TODAY("STORE109", "오늘은 정기 휴무일입니다", HttpStatus.BAD_REQUEST),
	
	// ----------------------------------------------------
	// SRP1xx: 예약 정책 (ReservationPolicy) 검증 오류
	// ----------------------------------------------------
	POLICY_ALREADY_EXISTS("SRP100", "해당 음식점의 예약 정책이 이미 등록되어 있습니다.", HttpStatus.BAD_REQUEST),
	POLICY_NOT_FOUND("SRP101", "해당 음식점의 예약 정책을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

	OPERATING_HOUR_MISSING("OH100", "운영 시간 정보가 시스템에 누락되었습니다. (Store 생성 시 7일 정보가 모두 필요합니다.)",
		HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
