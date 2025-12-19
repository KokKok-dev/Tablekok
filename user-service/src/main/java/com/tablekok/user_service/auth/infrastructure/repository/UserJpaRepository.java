package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUserId(UUID userId);

	boolean existsByEmail(String email);
}
