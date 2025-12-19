package com.tablekok.waiting_server.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerType {
	MEMBER("회원"),
	NON_MEMBER("비회원");

	private final String description;
}
