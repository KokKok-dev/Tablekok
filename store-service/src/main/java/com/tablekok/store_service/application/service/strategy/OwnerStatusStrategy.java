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
public class OwnerStatusStrategy implements StoreStatusTransitionStrategy {

	// Owner가 상태 변경을 시작할 수 없는 현재 상태 목록
	private final Set<StoreStatus> FORBIDDEN_CURRENT_STATUSES = Set.of(
		StoreStatus.PENDING_APPROVAL,
		StoreStatus.APPROVAL_REJECTED,
		StoreStatus.DECOMMISSIONED // 이 상태에서는 영구적으로 변경 불가
	);

	// Owner가 목표 상태로 설정할 수 없는 Master 전용 상태 목록
	private final Set<StoreStatus> FORBIDDEN_NEW_STATUSES = Set.of(
		StoreStatus.PENDING_APPROVAL,
		StoreStatus.APPROVAL_REJECTED
	);

	// Owner가 OPERATING 상태에서 설정할 수 있는 임시 상태 목록
	private final Set<StoreStatus> ALLOWED_TRANSITION_FROM_OPERATING = Set.of(
		StoreStatus.CLOSED_TODAY,
		StoreStatus.BREAK_TIME,
		StoreStatus.OPERATING, // 복구는 가능해야 함
		StoreStatus.DECOMMISSIONED // 영구 폐업 요청 가능
	);

	@Override
	public Boolean supports(String role) {
		return role.equals("OWNER");
	}

	@Override
	public void changeStatus(Store store, StoreStatus newStatus) {
		// 현재 상태가 PENDING/REJECTED/DECOMMISSIONED인 경우, Owner는 상태 변경 불가
		if (FORBIDDEN_CURRENT_STATUSES.contains(store.getStatus())) {
			throw new AppException(StoreErrorCode.OWNER_FORBIDDEN_CURRENT_STATUS_TRANSITION);
		}

		// Owner가 Master 전용 상태로 NEW STATUS를 시도 시 예외
		if (FORBIDDEN_NEW_STATUSES.contains(newStatus)) {
			throw new AppException(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION);
		}

		// 임시 상태에서 OPERATING으로의 복구는 허용
		if ((store.getStatus() == StoreStatus.CLOSED_TODAY || store.getStatus() == StoreStatus.BREAK_TIME)
			&& newStatus == StoreStatus.OPERATING) {
			store.changeStatus(newStatus);
			return;
		}

		// 현재 OPERATING 상태일 때 임시 상태/DECOMMISSIONED로의 전환만 허용
		if (store.getStatus() == StoreStatus.OPERATING) {
			if (ALLOWED_TRANSITION_FROM_OPERATING.contains(newStatus)) {
				store.changeStatus(newStatus);
				return;
			}
		}

		store.changeStatus(newStatus);
	}
}
