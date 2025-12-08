package com.tablekok.store_service.application.service.strategy;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MasterStatusStrategy implements StoreStatusTransitionStrategy {

	// Master가 변경할 수 있는 status
	private final Set<StoreStatus> ALLOWED_MASTER_STATUSES = Set.of(
		StoreStatus.PENDING_APPROVAL,
		StoreStatus.APPROVAL_REJECTED,
		StoreStatus.OPERATING,
		StoreStatus.DECOMMISSIONED
	);

	// Master가 절대 관여하지 않아야 할 Owner 전용 임시 상태
	private final Set<StoreStatus> OWNER_TEMP_STATUSES = Set.of(
		StoreStatus.CLOSED_TODAY,
		StoreStatus.BREAK_TIME
	);

	// 이미 승인되었거나 운영되었던 상태 목록 (PENDING_APPROVAL은 제외)
	Set<StoreStatus> FORBIDDEN_REVERSION_STATUSES = Set.of(
		StoreStatus.OPERATING,
		StoreStatus.DECOMMISSIONED
	);

	@Override
	public Boolean supports(String role) {
		return role.equals("MASTER");
	}

	@Override
	public void changeStatus(Store store, StoreStatus newStatus) {
		// New Status 검사 (Master의 권한 범위 밖의 상태로 전환 시도)
		if (!ALLOWED_MASTER_STATUSES.contains(newStatus)) {
			throw new AppException(StoreErrorCode.MASTER_INVALID_STATUS_TRANSITION);
		}

		// Current Status 검사 (Owner 전용 상태에 Master가 개입하는 것을 방지)
		// Master는 CLOSED_TODAY나 BREAK_TIME 상태 자체를 다루지 않습니다.
		if (OWNER_TEMP_STATUSES.contains(store.getStatus())) {
			throw new AppException(StoreErrorCode.MASTER_INVALID_STATUS_TRANSITION);
		}

		// 3. 역전환 로직 검사 (PENDING_APPROVAL로 되돌리는 행위 중 금지된 경우)
		if (FORBIDDEN_REVERSION_STATUSES.contains(store.getStatus()) && newStatus == StoreStatus.PENDING_APPROVAL) {
			throw new AppException(StoreErrorCode.MASTER_FORBIDDEN_REVERSION_TRANSITION);
		}

		store.changeStatus(newStatus);
	}
}
