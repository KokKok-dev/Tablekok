package com.tablekok.reservation_service.application.service.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StrategyFactory {
	private final List<RoleStrategy> strategies;

	// 요청된 역할(Role)에 맞는 전략을 찾아서 반환
	public RoleStrategy getStrategy(String role) {
		return strategies.stream()
			.filter(strategy -> strategy.supports(role)) // 해당 role을 지원하는지 확인
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 취소 역할입니다: " + role));
	}
}
