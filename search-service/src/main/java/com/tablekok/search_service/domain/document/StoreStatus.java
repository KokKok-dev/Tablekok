package com.tablekok.search_service.domain.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {

	PENDING_APPROVAL("관리자 승인 대기 중"),
	APPROVAL_REJECTED("관리자가 승인을 거부한 상태"),
	OPERATING("정상 운영 중 (승인 완료)"),
	CLOSED_TODAY("오늘 하루 영업 마감 (일시적)"),
	BREAK_TIME("휴업중 (일시적)"),
	DECOMMISSIONED("영구 폐업");

	private final String description;

	public boolean isSearchable() {
		return this == OPERATING || this == CLOSED_TODAY || this == BREAK_TIME;
	}
}
