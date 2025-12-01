package com.tablekok.store_service.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
	OPENING("영업중"),
	CLOSED("마감 (당일 영업 종료)"),
	BREAK("휴업중 (일시적)"),
	SHUTDOWN("폐업 (영구적)");

	private final String description;

}
