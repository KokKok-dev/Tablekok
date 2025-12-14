package com.tablekok.user_service.user.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileResult(
	UUID userId,
	String email,
	String username,
	String phoneNumber,
	String role,
	String businessNumber,
	LocalDateTime createdAt
) {
	public static ProfileResult of(User user, String businessNumber) {
		return new ProfileResult(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getPhoneNumber(),
			user.getRole().name(),
			businessNumber,
			user.getCreatedAt()
		);
	}
}
