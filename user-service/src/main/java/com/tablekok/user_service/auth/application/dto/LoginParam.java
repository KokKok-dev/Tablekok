// auth/application/dto/LoginParam.java
package com.tablekok.user_service.auth.application.dto;

import lombok.Builder;

/**
 * 로그인 Application Layer DTO (Param)
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 *
 * 순수 데이터 전송 객체 - 비즈니스 로직은 Service에서 처리
 */
@Builder
public record LoginParam(
	String email,
	String password
) {
	// 순수 데이터만 - 비즈니스 로직 제거
}
