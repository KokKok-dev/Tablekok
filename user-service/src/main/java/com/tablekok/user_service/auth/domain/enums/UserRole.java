// auth/domain/enums/UserRole.java
package com.tablekok.user_service.auth.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	CUSTOMER("ROLE_CUSTOMER", "일반 고객"),
	OWNER("ROLE_OWNER", "사장님"),
	MASTER("ROLE_MASTER", "마스터 관리자");

	private final String authority;
	private final String description;

	/**
	 * 권한 문자열로 UserRole 찾기
	 */
	public static UserRole fromAuthority(String authority) {
		for (UserRole role : values()) {
			if (role.authority.equals(authority)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown authority: " + authority);
	}

	/**
	 * Role 이름으로 UserRole 찾기
	 */
	public static UserRole fromName(String name) {
		for (UserRole role : values()) {
			if (role.name().equals(name)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role name: " + name);
	}
}
