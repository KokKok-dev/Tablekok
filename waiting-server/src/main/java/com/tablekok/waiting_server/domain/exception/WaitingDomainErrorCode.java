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
	WAITING_ALREADY_CLOSED("WAITING006", "이미 웨이팅 접수가 중단된 상태입니다.", HttpStatus.BAD_REQUEST);;

	private final String code;
	private final String message;
	private final HttpStatus status;
}
