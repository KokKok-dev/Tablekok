package com.tablekok.store_service.application.service.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;
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

	private static final Set<StoreStatus> FORBIDDEN_CURRENT_STATUSES = Set.of(
		StoreStatus.PENDING_APPROVAL,
		StoreStatus.APPROVAL_REJECTED,
		StoreStatus.DECOMMISSIONED
	);

	private static final Set<StoreStatus> ALLOWED_TRANSITION_FROM_OPERATING = Set.of(
		StoreStatus.CLOSED_TODAY,
		StoreStatus.BREAK_TIME,
		StoreStatus.DECOMMISSIONED
	);

	@BeforeEach
	void setUp() {
		this.store = Store.of(
			UUID.randomUUID(), "Test Store", "010-1234-5678", "Address",
			new BigDecimal("0"), new BigDecimal("0"), "Desc", 100, 30, "img.jpg"
		);
	}

	static Stream<StoreStatus> forbiddenCurrentStatuses() {
		return FORBIDDEN_CURRENT_STATUSES.stream();
	}

	@ParameterizedTest(name = "[Fail] 현재 상태: {0}일 때 Owner 변경 시도 시 예외 발생")
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

	static Stream<StoreStatus> temporaryStatusesForRecovery() {
		return Stream.of(StoreStatus.CLOSED_TODAY, StoreStatus.BREAK_TIME);
	}

	@ParameterizedTest(name = "[Success] 임시 상태 {0}에서 OPERATING으로 복구")
	@MethodSource("temporaryStatusesForRecovery")
	@DisplayName("임시 상태(CLOSED_TODAY, BREAK_TIME)에서 OPERATING으로의 복구는 성공해야 함")
	void changeStatus_TemporaryStatusToOperating_Success(StoreStatus currentStatus) {
		// Given
		this.store.changeStatus(currentStatus);
		StoreStatus targetStatus = StoreStatus.OPERATING;

		// When
		ownerStatusStrategy.changeStatus(store, targetStatus);

		// Then
		assertEquals(targetStatus, store.getStatus(), "임시 상태에서 OPERATING으로 상태가 성공적으로 변경되어야 합니다.");
	}

	static Stream<StoreStatus> allowedTransitionFromOperating() {
		return ALLOWED_TRANSITION_FROM_OPERATING.stream();
	}

	@ParameterizedTest(name = "[Success] OPERATING에서 허용된 임시 상태 {0}로 전환")
	@MethodSource("allowedTransitionFromOperating")
	@DisplayName("OPERATING 상태에서 허용된 임시 상태로의 전환은 성공해야 함")
	void changeStatus_OperatingToAllowedTempStatus_Success(StoreStatus targetStatus) {
		// Given
		this.store.changeStatus(StoreStatus.OPERATING);

		// When
		ownerStatusStrategy.changeStatus(store, targetStatus);

		// Then
		assertEquals(targetStatus, store.getStatus(), "OPERATING에서 허용된 상태로 성공적으로 전환되어야 합니다.");
	}
}
