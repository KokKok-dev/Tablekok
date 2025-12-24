package com.tablekok.waiting_server.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.query.Param;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

public interface StoreWaitingStatusRepository {
	Optional<StoreWaitingStatus> findById(UUID storeId);

	StoreWaitingStatus save(StoreWaitingStatus status);

	void resetAllStoresDaily();

	int updateLatestNumberIfGreater(@Param("storeId") UUID storeId, @Param("assignedNumber") int assignedNumber);
}
