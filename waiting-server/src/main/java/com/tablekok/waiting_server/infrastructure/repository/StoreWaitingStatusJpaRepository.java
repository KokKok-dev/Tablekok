package com.tablekok.waiting_server.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

import jakarta.persistence.LockModeType;

public interface StoreWaitingStatusJpaRepository extends JpaRepository<StoreWaitingStatus, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM StoreWaitingStatus s WHERE s.storeId = :storeId")
	Optional<StoreWaitingStatus> findByIdWithLock(@Param("storeId") UUID storeId);
}
