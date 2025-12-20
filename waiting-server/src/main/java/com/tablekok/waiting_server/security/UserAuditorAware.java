package com.tablekok.waiting_server.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.tablekok.dto.auth.AuthUser;

@Component
public class UserAuditorAware implements AuditorAware<UUID> {

	@Override
	public Optional<UUID> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		// HeaderAuthFilter에서 넣었던 AuthUser 객체를 꺼냅니다.
		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthUser authUser) {
			return Optional.of(UUID.fromString(authUser.userId()));
		}

		return Optional.empty();
	}
}
