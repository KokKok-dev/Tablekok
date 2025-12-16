package com.tablekok.waiting_server.infrastructure.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;

public interface WaitingJpaRepository extends JpaRepository<Waiting, UUID> {
	Optional<Waiting> findByIdAndStoreId(UUID waitingId, UUID storeId);

	List<Waiting> findAllByIdIn(Collection<UUID> waitingIds);

	// 특정 매장에서 특정 회원이 주어진 status 들 중 하나에 해당하는 웨이팅이 있는지 확인
	boolean existsByStoreIdAndMemberIdAndStatusIn(
		UUID storeId,
		UUID memberId,
		Collection<WaitingStatus> status
	);

	// 특정 매장에서, 특정 비회원 전화번호가, 주어진 status 들 중 하나에 해당하는 웨이팅이 있는지 확인
	boolean existsByStoreIdAndNonMemberPhoneAndStatusIn(
		UUID storeId,
		String nonMemberPhone,
		Collection<WaitingStatus> status
	);
}
