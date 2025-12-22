package com.tablekok.waiting_server.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

public interface StoreWaitingStatusRepository {
	Optional<StoreWaitingStatus> findByIdWithLock(UUID storeId);

	Optional<StoreWaitingStatus> findById(UUID storeId);

	StoreWaitingStatus save(StoreWaitingStatus status);

	void resetAllStoresDaily();
}
