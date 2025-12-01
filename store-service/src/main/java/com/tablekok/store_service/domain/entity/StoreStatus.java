package com.tablekok.store_service.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
	OPENING("OPENING"),
	CLOSED("CLOSED"),
	BREAK("BREAK"),
	SHUTDOWN("SHUTDOWN");

	private final String value;

}
