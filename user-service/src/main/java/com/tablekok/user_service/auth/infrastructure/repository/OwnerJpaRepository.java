package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerJpaRepository extends JpaRepository<Owner, UUID> {

	Optional<Owner> findByUserId(UUID userId);

	boolean existsByBusinessNumber(String businessNumber);

	List<Owner> findByUserIdIn(List<UUID> userIds);
}
