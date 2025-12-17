package com.tablekok.user_service.user.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tablekok.user_service.user.application.dto.result.UpdateProfileResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateProfileResponse(
	UUID userId,
	String email,
	String username,
	String phoneNumber,
	String role,
	String businessNumber,
	LocalDateTime updatedAt
) {
	public static UpdateProfileResponse from(UpdateProfileResult result) {
		return UpdateProfileResponse.builder()
			.userId(result.userId())
			.email(result.email())
			.username(result.username())
			.phoneNumber(result.phoneNumber())
			.role(result.role())
			.businessNumber(result.businessNumber())
			.updatedAt(result.updatedAt())
			.build();
	}
}
