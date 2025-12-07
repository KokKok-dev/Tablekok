package com.tablekok.store_service.application.service.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;

class OwnerStatusStrategyTest {

	private final OwnerStatusStrategy ownerStatusStrategy = new OwnerStatusStrategy();

	private Store store;

	@BeforeEach
	void setUp() {
		this.store = Store.of(
			UUID.randomUUID(), "Test Store", "010-1234-5678", "Address",
			new BigDecimal("0"), new BigDecimal("0"), "Desc", 100, 30, "img.jpg"
		);
	}

	static Stream<StoreStatus> forbiddenCurrentStatuses() {
		return Stream.of(
			StoreStatus.PENDING_APPROVAL,
			StoreStatus.APPROVAL_REJECTED,
			StoreStatus.DECOMMISSIONED
		);
	}

	@ParameterizedTest
	@MethodSource("forbiddenCurrentStatuses")
	@DisplayName("Master 만 변경할 수 있는 상태일 때 Owner 가 상태 변경 시도 시 예외 발생")
	void changeStatus_CurrentStatusIsForbidden_ThrowsException(StoreStatus nowStatus) {
		// Given
		this.store.changeStatus(nowStatus);
		StoreStatus targetStatus = StoreStatus.OPERATING;

		// When & Then
		AppException exception = assertThrows(AppException.class, () -> {
			ownerStatusStrategy.changeStatus(store, targetStatus);
		});

		assertEquals(StoreErrorCode.OWNER_FORBIDDEN_CURRENT_STATUS_TRANSITION, exception.getErrorCode());

	}

}
