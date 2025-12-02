package com.tablekok.reservation_service.application.service;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
	RESERVATION_NOT_FOUND(
		"RESERVATION100",
		"예약을 찾을 수 없습니다.",
		HttpStatus.NOT_FOUND),

	FORBIDDEN_STORE_ACCESS(
		"RESERVATION101",
		"해당 음식점에 대한 권한이 없습니다.",
		HttpStatus.FORBIDDEN);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
