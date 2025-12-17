package com.tablekok.user_service.user.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateProfileResult(
	UUID userId,
	String email,
	String username,
	String phoneNumber,
	String role,
	String businessNumber,
	LocalDateTime updatedAt
) {
	public static UpdateProfileResult of(User user, String businessNumber) {
		return UpdateProfileResult.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.username(user.getUsername())
			.phoneNumber(user.getPhoneNumber())
			.role(user.getRole().name())
			.businessNumber(businessNumber)
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
