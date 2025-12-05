package com.tablekok.user_service.auth.application.dto.command;

import lombok.Builder;

/**
 * 로그인 Application Layer DTO (Command)
 * 코드 컨벤션: Process<Entity>Command 패턴 적용
 *
 * 순수 데이터 전송 객체 - 비즈니스 로직은 Service에서 처리
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 */
@Builder
public record LoginCommand(
	String email,
	String password
) {
	// 단순 데이터 전송만 담당
	// 검증 및 비즈니스 로직은 AuthDomainService에서 수행
}
