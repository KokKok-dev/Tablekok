package com.tablekok.user_service.auth.domain.repository;

import com.tablekok.user_service.auth.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

	User save(User user);

	Optional<User> findByEmail(String email);

	Optional<User> findByUserId(UUID userId);

	boolean existsByEmail(String email);
}
