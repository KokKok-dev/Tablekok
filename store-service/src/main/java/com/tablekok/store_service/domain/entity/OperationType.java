package com.tablekok.store_service.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {
	CREATE("생성"),
	UPDATE("정보 수정"),
	STATUS_CHANGE("상태 수정"),
	DELETE("삭제");

	private final String description;
}
