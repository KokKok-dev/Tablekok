package com.tablekok.waiting_server.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WaitingRepositoryAdapter implements WaitingRepository {
	private final WaitingJpaRepository waitingJpaRepository;

	@Override
	public Waiting save(Waiting waiting) {
		return waitingJpaRepository.save(waiting);
	}

	@Override
	public Optional<Waiting> findById(UUID waitingId) {
		return waitingJpaRepository.findById(waitingId);
	}

	@Override
	public Optional<Waiting> findByIdAndStoreId(UUID waitingId, UUID storeId) {
		return waitingJpaRepository.findByIdAndStoreId(waitingId, storeId);
	}

	@Override
	public List<Waiting> findAllByIdIn(List<UUID> waitingIds) {
		return waitingJpaRepository.findAllByIdIn(waitingIds);
	}

}
