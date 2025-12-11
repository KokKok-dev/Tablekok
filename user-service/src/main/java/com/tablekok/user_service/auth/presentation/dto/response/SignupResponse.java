package com.tablekok.user_service.auth.presentation.dto.response;

import com.tablekok.user_service.auth.application.dto.result.SignupResult;

import java.util.UUID;

public record SignupResponse(
	UUID userId,
	String email,
	String username,
	String role,
	String accessToken
) {
	public static SignupResponse from(SignupResult result) {
		return new SignupResponse(
			result.userId(),
			result.email(),
			result.username(),
			result.role(),
			result.accessToken()
		);
	}
}
