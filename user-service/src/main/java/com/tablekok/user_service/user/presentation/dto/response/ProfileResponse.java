package com.tablekok.user_service.user.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileResponse (
	UUID userId,
	String email,
	String username,
	String phoneNumber,
	String role,
	String businessNumber,
	LocalDateTime createdAt
) {
	public static ProfileResponse from(ProfileResult result) {
		return new ProfileResponse(
			result.userId(),
			result.email(),
			result.username(),
			result.phoneNumber(),
			result.role(),
			result.businessNumber(),
			result.createdAt()
		);
	}
}
