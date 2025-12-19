package com.tablekok.user_service.user.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDetailResult(
	UUID userId,
	String username,
	String email,
	String phoneNumber,
	String businessNumber,
	String role,
	boolean isActive,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	int loginCount
) {
	public static UserDetailResult of(User user, String businessNumber) {
		return UserDetailResult.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.businessNumber(businessNumber)
			.role(user.getRole().name())
			.isActive(user.isActive())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.loginCount(user.getLoginCount())
			.build();
	}
}
