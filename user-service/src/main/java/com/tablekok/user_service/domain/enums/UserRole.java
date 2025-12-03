package com.tablekok.user_service.domain.enums;

public enum UserRole {
	CUSTOMER("CUSTOMER"),
	OWNER("OWNER"),
	MASTER("MASTER");

	private final String value;

	UserRole(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	// String 값으로 UserRole을 찾는 유틸리티 메서드
	public static UserRole fromValue(String value) {
		for (UserRole role : UserRole.values()) {
			if (role.getValue().equalsIgnoreCase(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + value);
	}
}
