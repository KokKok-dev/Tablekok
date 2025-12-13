package com.tablekok.waiting_server.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreWaitingStatusRepositoryAdapter implements StoreWaitingStatusRepository {

	private final StoreWaitingStatusJpaRepository storeWaitingStatusJpaRepository;

	@Override
	public Optional<StoreWaitingStatus> findByIdWithLock(UUID storeId) {
		return storeWaitingStatusJpaRepository.findByIdWithLock(storeId);
	}

	@Override
	public Optional<StoreWaitingStatus> findById(UUID storeId) {
		return storeWaitingStatusJpaRepository.findById(storeId);
	}

	@Override
	public StoreWaitingStatus save(StoreWaitingStatus status) {
		return storeWaitingStatusJpaRepository.save(status);
	}
}
