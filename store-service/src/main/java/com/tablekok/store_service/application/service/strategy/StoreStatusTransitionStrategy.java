package com.tablekok.store_service.application.service.strategy;

import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;

public interface StoreStatusTransitionStrategy {
	Boolean supports(String role);

	void changeStatus(Store store, StoreStatus newStatus);
}
