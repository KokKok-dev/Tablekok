package com.tablekok.store_service.application.service.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

	@ParameterizedTest(name = "[Fail] 현재 상태: {0}일 때 Owner 변경 시도 시 예외 발생")
	@CsvSource({
		// [1] master만 변경할 수 있는 상태에서 OPERATING 시도
		"PENDING_APPROVAL, OPERATING",
		"APPROVAL_REJECTED, OPERATING",
		"DECOMMISSIONED, OPERATING",

		// [2] master만 변경할 수 있는 상태에서 CLOSED_TODAY 시도
		"PENDING_APPROVAL, CLOSED_TODAY",
		"APPROVAL_REJECTED, CLOSED_TODAY",
		"DECOMMISSIONED, CLOSED_TODAY",

		// [3] master만 변경할 수 있는 상태에서 BREAK_TIME 시도
		"PENDING_APPROVAL, BREAK_TIME",
		"APPROVAL_REJECTED, BREAK_TIME",
		"DECOMMISSIONED, BREAK_TIME",
	})
	@DisplayName("Master 만 변경할 수 있는 상태일 때 Owner 가 상태 변경 시도 시 예외 발생")
	void changeStatus_CurrentStatusIsForbidden_ThrowsException(StoreStatus startStatus, StoreStatus targetStatuss) {
		// Given
		this.store.changeStatus(startStatus);

		// When & Then
		AppException exception = assertThrows(AppException.class, () -> {
			ownerStatusStrategy.changeStatus(store, targetStatuss);
		});

		assertEquals(StoreErrorCode.OWNER_FORBIDDEN_CURRENT_STATUS_TRANSITION, exception.getErrorCode());

	}

	static Stream<StoreStatus> forbiddenNewStatuses() {
		return Stream.of(
			StoreStatus.PENDING_APPROVAL,
			StoreStatus.APPROVAL_REJECTED
		);
	}

	@ParameterizedTest(name = "[Fail] NEW STATUS: {0} 시도 시 예외 발생")
	@MethodSource("forbiddenNewStatuses")
	@DisplayName("Master 만 변경할 수 있는 상태 시도 시 예외 발생")
	void changeStatus_NewStatusIsForbidden_ThrowsException(StoreStatus newStatus) {
		// Given : OPERATING 상태일 때
		this.store.changeStatus(StoreStatus.OPERATING);

		// When & Then
		AppException exception = assertThrows(AppException.class, () -> {
			ownerStatusStrategy.changeStatus(store, newStatus);
		});

		assertEquals(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION, exception.getErrorCode());
	}

	// ================ 성공 케이스 ================

	@ParameterizedTest(name = "[Success] {0} -> {1} 전환 성공")
	@CsvSource({
		// [1] 임시 상태에서 OPERATING으로 복구
		"CLOSED_TODAY, OPERATING",
		"BREAK_TIME, OPERATING",

		// [2] 임시상태에서 임시상태로 변경 가능
		"BREAK_TIME, CLOSED_TODAY",
		"CLOSED_TODAY, BREAK_TIME",

		// [3] OPERATING 상태에서 허용된 상태로 전환
		"OPERATING, CLOSED_TODAY",
		"OPERATING, BREAK_TIME",
		"OPERATING, DECOMMISSIONED"
	})
	@DisplayName("[1] & [2] & [3]. 허용된 모든 성공 전환은 상태를 변경해야 함")
	void changeStatus_AllowedTransitions_Success(StoreStatus startStatus, StoreStatus targetStatus) {
		// Given
		this.store.changeStatus(startStatus);

		// When
		ownerStatusStrategy.changeStatus(store, targetStatus);

		// Then
		assertEquals(targetStatus, store.getStatus(),
			startStatus + "에서 " + targetStatus + "로 상태가 성공적으로 변경되어야 합니다.");
	}

}
