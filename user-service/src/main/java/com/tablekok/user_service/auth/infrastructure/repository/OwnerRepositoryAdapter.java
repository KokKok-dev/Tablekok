package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepository {

	private final OwnerJpaRepository ownerJpaRepository;

	@Override
	public Owner save(Owner owner) {
		return ownerJpaRepository.save(owner);
	}

	@Override
	public Optional<Owner> findByUserId(UUID userId) {
		return ownerJpaRepository.findByUserId(userId);
	}

	@Override
	public boolean existsByBusinessNumber(String businessNumber) {
		return ownerJpaRepository.existsByBusinessNumber(businessNumber);
	}

	@Override
	public List<Owner> findByUserIdIn(List<UUID> userIds) {
		return ownerJpaRepository.findByUserIdIn(userIds);
	}
}
