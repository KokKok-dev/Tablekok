package com.tablekok.user_service.auth.domain.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository {

	Owner save(Owner owner);

	Optional<Owner> findByUserId(UUID userId);

	boolean existsByBusinessNumber(String businessNumber);
}
