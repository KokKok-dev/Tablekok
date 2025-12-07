package com.tablekok.store_service.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreDomainErrorCode implements ErrorCode {

	// ----------------------------------------------------
	// STORE0xx: 음식점 관련 오류
	// ----------------------------------------------------
	INVALID_CATEGORY_ID("STORE000", "유효하지 않은 카테고리 ID입니다.", HttpStatus.BAD_REQUEST),
	INVALID_STORE_STATUS("STORE001", "현재 음식점 상태에서는 예약 정책 등록이 불가능합니다.", HttpStatus.BAD_REQUEST),

	// ----------------------------------------------------
	// OH0xx: 운영 시간 (OperatingHour) 검증 오류
	// ----------------------------------------------------
	DUPLICATE_OPERATING_DAY("OH000", "하나의 요일에 대해 두 개 이상의 영업 시간 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
	MISSING_ALL_OPERATING_DAYS("OH001", "필수 요일 정보가 불완전합니다. 월요일부터 일요일까지 모든 요일 정보가 포함되어야 합니다.",
		HttpStatus.BAD_REQUEST),
	INVALID_CLOSED_TIME("OH002", "휴무일(isClosed=true) 설정 시 영업 시간(openTime, closeTime)을 지정할 수 없습니다.",
		HttpStatus.BAD_REQUEST),
	MISSING_OPERATING_TIME("OH003", "영업일(isClosed=false) 설정 시 시작 시간과 종료 시간은 필수입니다.", HttpStatus.BAD_REQUEST),
	INVALID_TIME_RANGE("OH004", "영업 종료 시간은 시작 시간보다 늦어야 합니다.", HttpStatus.BAD_REQUEST), // OperatingHour 시간 범위 오류

	// ----------------------------------------------------
	// RP0xx: 예약 정책 (ReservationPolicy) 검증 오류
	// ----------------------------------------------------
	INVALID_OPEN_DAY("RP000", "예약 오픈 날짜(monthlyOpenDay)는 1일부터 28일까지만 유효합니다.", HttpStatus.BAD_REQUEST),
	INVALID_RESERVATION_INTERVAL("RP001", "예약 간격은 10, 15, 20, 30, 60, 120분 중 하나여야 합니다.", HttpStatus.BAD_REQUEST),

	INVALID_POLICY_TIME_RANGE("RP002", "예약 마감 시간은 시작 시간보다 이후여야 합니다.", HttpStatus.BAD_REQUEST),
	RESERVATION_TIME_BEFORE_STORE_OPEN("RP003", "예약 가능 시작 시간은 매장 운영 시작 시간보다 빠를 수 없습니다.", HttpStatus.BAD_REQUEST),
	INSUFFICIENT_TIME_SLOT("RP004", "예약 가능 시간 범위가 최소 예약 간격보다 짧아 슬롯 생성이 불가능합니다.", HttpStatus.BAD_REQUEST),

	INVALID_HEADCOUNT_RANGE("RP005", "최대 예약 인원수는 최소 예약 인원수보다 크거나 같아야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_DEPOSIT_AMOUNT("RP006", "선예약금 필수 시, 예약금은 0보다 큰 값이어야 합니다.", HttpStatus.BAD_REQUEST),

	RESERVATION_TIME_BEFORE_OPERATING_OPEN("RP007", "예약 가능 시작 시간이 실제 운영 시작 시간보다 빠를 수 없습니다.",
		HttpStatus.BAD_REQUEST),
	RESERVATION_TIME_AFTER_OPERATING_CLOSE("RP008", "예약 가능 마감 시간이 실제 운영 종료 시간보다 늦을 수 없습니다.",
		HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
