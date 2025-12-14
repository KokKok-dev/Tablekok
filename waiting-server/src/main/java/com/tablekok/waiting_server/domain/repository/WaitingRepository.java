package com.tablekok.waiting_server.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.Waiting;

public interface WaitingRepository {

	Waiting save(Waiting waiting);

	Optional<Waiting> findById(UUID waitingId);
}
