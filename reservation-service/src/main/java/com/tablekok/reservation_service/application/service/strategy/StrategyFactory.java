package com.tablekok.reservation_service.application.service.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StrategyFactory {
	private final List<RoleStrategy> strategies;

	// 요청된 역할(Role)에 맞는 전략을 찾아서 반환
	public RoleStrategy getStrategy(UserRole role) {
		return strategies.stream()
			.filter(strategy -> strategy.supports(role)) // 해당 role을 지원하는지 확인
			.findFirst()
			.orElseThrow(() -> new AppException(ReservationErrorCode.FORBIDDEN_ACCESS));
	}
}
