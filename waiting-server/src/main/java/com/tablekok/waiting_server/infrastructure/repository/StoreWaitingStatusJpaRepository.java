package com.tablekok.waiting_server.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

public interface StoreWaitingStatusJpaRepository extends JpaRepository<StoreWaitingStatus, UUID> {
	@Modifying
	@Query("UPDATE StoreWaitingStatus s " +
		"SET s.latestAssignedNumber = 0, " +
		"    s.currentCallingNumber = 0, " +
		"    s.isWaitingEnabled = false")
	void resetAllStoresDaily();

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE StoreWaitingStatus s SET s.latestAssignedNumber = :assignedNumber " +
		"WHERE s.storeId = :storeId AND s.latestAssignedNumber < :assignedNumber")
	int updateLatestNumberIfGreater(@Param("storeId") UUID storeId, @Param("assignedNumber") int assignedNumber);
}
