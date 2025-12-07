package com.tablekok.store_service.application.service.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.exception.StoreErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StrategyFactory {
	private final List<StoreStatusTransitionStrategy> strategies;

	public StoreStatusTransitionStrategy getStrategy(String role) {
		return strategies.stream()
			.filter(strategy -> strategy.supports(role))
			.findFirst()
			.orElseThrow(() -> new AppException(StoreErrorCode.UNSUPPORTED_USER_ROLE));
	}
}
