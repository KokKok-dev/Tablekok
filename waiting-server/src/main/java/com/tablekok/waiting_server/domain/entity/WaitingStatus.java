package com.tablekok.waiting_server.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingStatus {

	WAITING("웨이팅 중"),
	CALLED("사장님이 입장하라고 알림"),
	CONFIRMED("사용자가 가겠다고 응답"),
	ENTERED("입장"),
	OWNER_CANCELED("사장님 취소"),
	USER_CANCELED("유저 취소"),
	NO_SHOW("간다하고 안온 경우");

	private final String description;
}
