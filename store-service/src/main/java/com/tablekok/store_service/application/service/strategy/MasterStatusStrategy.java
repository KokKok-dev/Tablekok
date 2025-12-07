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

	private final Set<StoreStatus> ALLOWED_MASTER_STATUSES = Set.of(
		StoreStatus.PENDING_APPROVAL,
		StoreStatus.APPROVAL_REJECTED,
		StoreStatus.OPERATING,
		StoreStatus.DECOMMISSIONED
	);

	@Override
	public Boolean supports(String role) {
		return role.equals("MASTER");
	}

	@Override
	public void changeStatus(Store store, StoreStatus newStatus) {
		if (!ALLOWED_MASTER_STATUSES.contains(newStatus)) {
			throw new AppException(StoreErrorCode.MASTER_INVALID_STATUS_TRANSITION);
		}
		store.changeStatus(newStatus);
	}
}
