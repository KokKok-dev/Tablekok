package com.tablekok.hotreservationservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationDomainErrorCode implements ErrorCode {
	HOT_STORE_RESERVATION_NOT_ALLOWED(
		"RESERVATION000",
		"인기 음식점의 예약만 가능합니다.",
		HttpStatus.BAD_REQUEST),

	STORE_RESERVATION_DISABLED(
		"RESERVATION001",
		"해당 음식점은 현재 예약을 받을 수 없습니다.",
		HttpStatus.BAD_REQUEST
	),

	RESERVATION_NOT_AVAILABLE_MONTH(
		"RESERVATION002",
		"예약 가능한 월이 아닙니다.",
		HttpStatus.BAD_REQUEST
	),

	RESERVATION_NOT_OPENED_YET(
		"RESERVATION003",
		"아직 다음 달 예약이 열리지 않았습니다.",
		HttpStatus.BAD_REQUEST
	),

	DUPLICATE_RESERVATION_TIME(
		"RESERVATION004",
		"이미 예약이 있는 시간입니다.",
		HttpStatus.CONFLICT
	),

	INVALID_RESERVATION_POLICY(
		"RESERVATION005",
		"예약 조건이 정책에 맞지 않습니다.",
		HttpStatus.BAD_REQUEST
	),

	PAST_RESERVATION_NOT_ALLOWED(
		"RESERVATION006",
		"과거 시간에는 예약할 수 없습니다.",
		HttpStatus.BAD_REQUEST
	);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
