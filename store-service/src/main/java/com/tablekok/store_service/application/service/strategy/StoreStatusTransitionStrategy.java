package com.tablekok.store_service.application.service.strategy;

import com.tablekok.entity.UserRole;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;

public interface StoreStatusTransitionStrategy {
	Boolean supports(UserRole role);

	void changeStatus(Store store, StoreStatus newStatus);
}
