package com.tablekok.waiting_server.infrastructure.repository;

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
}
