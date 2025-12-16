package com.tablekok.user_service.user.presentation.dto.response;

import com.tablekok.user_service.user.application.dto.result.UserDetailResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDetailResponse(
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
	public static UserDetailResponse from(UserDetailResult result) {
		return UserDetailResponse.builder()
			.userId(result.userId())
			.username(result.username())
			.email(result.email())
			.phoneNumber(result.phoneNumber())
			.businessNumber(result.businessNumber())
			.role(result.role())
			.isActive(result.isActive())
			.createdAt(result.createdAt())
			.updatedAt(result.updatedAt())
			.loginCount(result.loginCount())
			.build();
	}
}
