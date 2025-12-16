package com.tablekok.waiting_server.infrastructure.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.waiting_server.domain.entity.Waiting;

public interface WaitingJpaRepository extends JpaRepository<Waiting, UUID> {
	Optional<Waiting> findByIdAndStoreId(UUID waitingId, UUID storeId);

	List<Waiting> findAllByIdIn(Collection<UUID> waitingIds);
}
