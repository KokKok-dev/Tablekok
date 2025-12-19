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

class MasterStatusStrategyTest {
	private final MasterStatusStrategy masterStatusStrategy = new MasterStatusStrategy();

	private Store store;

	@BeforeEach
	void setUp() {
		this.store = Store.of(
			UUID.randomUUID(), "Test Store", "010-1234-5678", "Address",
			new BigDecimal("0"), new BigDecimal("0"), "Desc", 100, 30, "img.jpg"
		);
	}

	@ParameterizedTest(name = "[Success] {0} -> {1} 전환 성공 (마스터 전용)")
	@CsvSource({
		"PENDING_APPROVAL, OPERATING",         // 1. 승인 (Approval)
		"PENDING_APPROVAL, APPROVAL_REJECTED",  // 2. 거부 (Reject)
		"OPERATING, DECOMMISSIONED",            // 3. 폐점 (Decommission)
		"APPROVAL_REJECTED, OPERATING",         // 4. 거부 상태에서 재승인
		"DECOMMISSIONED, OPERATING"             // 5. 폐점 상태에서 복구/재개점 (마스터는 가능해야 함)
	})
	@DisplayName("Master 전용 상태 전환은 성공해야 함")
	void changeStatus_MasterExclusiveTransitions_Success(StoreStatus startStatus, StoreStatus targetStatus) {
		// Given
		this.store.changeStatus(startStatus);

		// When
		masterStatusStrategy.changeStatus(store, targetStatus);

		// Then
		assertEquals(targetStatus, store.getStatus(),
			startStatus + " 상태에서 " + targetStatus + "로의 전환이 마스터에 의해 허용되어야 합니다.");
	}

	@ParameterizedTest(name = "[Fail] {0} -> {1} 전환 성공 ((Master에게 금지된 Owner 운영 상태 관여))")
	@CsvSource({
		"CLOSED_TODAY, OPERATING",    // 임시 상태에서 복구
		"BREAK_TIME, OPERATING",      // 임시 상태에서 복구
		"OPERATING, CLOSED_TODAY",    // 운영 중 임시 휴무
		"OPERATING, BREAK_TIME",      // 운영 중 브레이크 타임
		"CLOSED_TODAY, BREAK_TIME",
		"BREAK_TIME, CLOSED_TODAY",
	})
	@DisplayName("Master는 Owner의 일시적 운영 상태에 관여할 수 없으며 예외 발생")
	void changeStatus_AttemptOwnerOperationalControl_ThrowsMasterInvalidTransitionException(StoreStatus startStatus,
		StoreStatus targetStatus) {
		// Given
		this.store.changeStatus(startStatus);

		// When & Then
		AppException exception = assertThrows(AppException.class, () -> {
			masterStatusStrategy.changeStatus(store, targetStatus);
		});

		assertEquals(StoreErrorCode.MASTER_INVALID_STATUS_TRANSITION, exception.getErrorCode(),
			"Master는 New Status (" + targetStatus + ")를 허용하지 않거나, 이 로직을 다룰 권한이 없습니다.");
	}

	static Stream<StoreStatus> invalidStartStatuses() {
		return Stream.of(StoreStatus.OPERATING, StoreStatus.DECOMMISSIONED);
	}

	@ParameterizedTest(name = "[Fail] {0} -> PENDING_APPROVAL 전환 시도 시 예외 발생")
	@MethodSource("invalidStartStatuses")
	@DisplayName("Master라도 이미 운영 중인 상태에서 PENDING_APPROVAL로의 전환은 금지되어야 함")
	void changeStatus_ToPendingApprovalFromOperational_ThrowsException(StoreStatus currentStatus) {
		// Given
		this.store.changeStatus(currentStatus);
		StoreStatus targetStatus = StoreStatus.PENDING_APPROVAL;

		// When & Then
		AppException exception = assertThrows(AppException.class, () -> {
			masterStatusStrategy.changeStatus(store, targetStatus);
		});

		assertEquals(StoreErrorCode.MASTER_FORBIDDEN_REVERSION_TRANSITION, exception.getErrorCode(),
			currentStatus + " 상태에서 PENDING_APPROVAL로의 역전환은 금지되어야 합니다.");
	}
}
