package com.tablekok.store_service.application.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {

	DUPLICATE_STORE_ENTRY("STORE100", "중복된 음식점입니다.", HttpStatus.BAD_REQUEST),
	// 정책 일관성 및 상태 오류
	STORE_NOT_FOUND("STORE404", "음식점을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	POLICY_ALREADY_EXISTS("STORE102", "해당 음식점의 예약 정책이 이미 등록되어 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_STORE_STATUS("STORE103", "현재 음식점 상태에서는 예약 정책 등록이 불가능합니다.", HttpStatus.BAD_REQUEST),

	// OperatingHour 관련 오류 (이전 대화에서 언급된 내용 포함)
	INVALID_TIME_RANGE("STORE202", "운영 종료 시간은 시작 시간보다 이후여야 합니다.", HttpStatus.BAD_REQUEST),

	// ----------------------------------------------------
	// ReservationPolicy 오류 (POLICY2xx) - 정책 생성/수정 시 검증 오류
	// ----------------------------------------------------
	INVALID_OPEN_DAY("POLICY200", "예약 오픈 날짜(monthlyOpenDay)는 1일부터 28일까지만 유효합니다.", HttpStatus.BAD_REQUEST),
	INVALID_RESERVATION_INTERVAL("POLICY201", "예약 간격은 10, 15, 20, 30, 60, 120분 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),

	INVALID_POLICY_TIME_RANGE("POLICY202", "예약 마감 시간은 시작 시간보다 이후여야 합니다.", HttpStatus.BAD_REQUEST),
	RESERVATION_TIME_BEFORE_STORE_OPEN("POLICY203", "예약 가능 시작 시간은 매장 운영 시작 시간보다 빠를 수 없습니다.", HttpStatus.BAD_REQUEST),
	INSUFFICIENT_TIME_SLOT("POLICY204", "예약 가능 시간 범위가 최소 예약 간격보다 짧아 슬롯 생성이 불가능합니다.", HttpStatus.BAD_REQUEST),

	INVALID_HEADCOUNT_RANGE("POLICY205", "최대 예약 인원수는 최소 예약 인원수보다 크거나 같아야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_DEPOSIT_AMOUNT("POLICY206", "선예약금 필수 시, 예약금은 0보다 큰 값이어야 합니다.", HttpStatus.BAD_REQUEST),

	RESERVATION_TIME_BEFORE_OPERATING_OPEN("POLICY207", "예약 가능 시작 시간이 실제 운영 시작 시간보다 빠를 수 없습니다.",
		HttpStatus.BAD_REQUEST),
	RESERVATION_TIME_AFTER_OPERATING_CLOSE("POLICY208", "예약 가능 마감 시간이 실제 운영 종료 시간보다 늦을 수 없습니다.",
		HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
