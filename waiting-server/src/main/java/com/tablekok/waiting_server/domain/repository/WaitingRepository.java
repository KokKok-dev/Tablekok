package com.tablekok.waiting_server.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;

public interface WaitingRepository {

	Waiting save(Waiting waiting);

	Optional<Waiting> findById(UUID waitingId);

	Optional<Waiting> findByIdAndStoreId(UUID waitingId, UUID storeId);

	List<Waiting> findAllByIdIn(List<UUID> waitingIds);

	boolean existsByStoreIdAndMemberIdAndStatusIn(UUID storeId, UUID memberId, Collection<WaitingStatus> status);

	boolean existsByStoreIdAndNonMemberPhoneAndStatusIn(UUID storeId, String nonMemberPhone,
		Collection<WaitingStatus> status);
}
