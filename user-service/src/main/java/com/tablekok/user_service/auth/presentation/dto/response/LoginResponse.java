package com.tablekok.user_service.auth.presentation.dto.response;

import com.tablekok.user_service.auth.application.dto.result.LoginResult;
import java.util.UUID;
public record LoginResponse (
	UUID userId,
	String email,
	String username,
	String role
) {
	public static LoginResponse from(LoginResult result) {
		return new LoginResponse(
			result.userId(),
			result.email(),
			result.username(),
			result.role()
		);
	}
}

