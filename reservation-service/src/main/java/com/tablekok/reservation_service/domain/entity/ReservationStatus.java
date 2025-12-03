package com.tablekok.reservation_service.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

	PENDING("예약금 결재 대기"),
	RESERVED("예약된 상태"),
	DONE("완료된 예약"),
	CANCELED("취소된 예약"),
	REJECT("오너가 취소한 예약"),
	NOSHOW("고객이 방문하지 않음");

	private final String description;
}
