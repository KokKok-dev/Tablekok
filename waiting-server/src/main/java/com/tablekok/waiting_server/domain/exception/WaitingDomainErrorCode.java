package com.tablekok.waiting_server.domain.exception;

import org.springframework.http.HttpStatus;

import com.tablekok.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingDomainErrorCode implements ErrorCode {
	WAITING_CLOSED("WAITING000", "현재 매장이 웨이팅 접수 중이 아닙니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_BELOW_MIN("WAITING001", "최소 인원수보다 이상이여야 합니다.", HttpStatus.BAD_REQUEST),
	HEADCOUNT_ABOVE_MAX("WAITING002", "최대 인원수보다 이하여야 합니다.", HttpStatus.BAD_REQUEST),
	MEMBER_ID_REQUIRED("WAITING003", "회원(MEMBER) 타입은 memberId가 필수입니다.", HttpStatus.BAD_REQUEST),
	NON_MEMBER_INFO_REQUIRED("WAITING004", "비회원(NON_MEMBER) 타입은 이름과 전화번호가 필수입니다.", HttpStatus.BAD_REQUEST),
	WAITING_ALREADY_STARTED("WAITING005", "매장 웨이팅은 이미 시작되었습니다.", HttpStatus.BAD_REQUEST),
	WAITING_ALREADY_CLOSED("WAITING006", "이미 웨이팅 접수가 중단된 상태입니다.", HttpStatus.BAD_REQUEST),
	INVALID_WAITING_STATUS("WAITING007", "웨이팅 상태가 유효하지 않아 요청을 처리할 수 없습니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_MEMBER_WAITING("WAITING008", "이미 해당 매장에 웨이팅을 요청하셨습니다.", HttpStatus.BAD_REQUEST),
	INVALID_CUSTOMER_TYPE("WAITING009", "유효하지 않은 고객 타입이 전달되었습니다.", HttpStatus.BAD_REQUEST),
	PHONE_NUMBER_REQUIRED("WAITING009", "비회원(NON_MEMBER) 타입은 전화번호가 필수입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATE_NON_MEMBER_WAITING("WAITING010", "이미 해당 매장에 전화번호로 웨이팅을 요청하셨습니다.", HttpStatus.BAD_REQUEST),
	NO_STORE_OWNER("WAITING11", "해당 매장의 소유주가 아닙니다.", HttpStatus.FORBIDDEN);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
