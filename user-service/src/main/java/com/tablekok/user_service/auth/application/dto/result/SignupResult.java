package com.tablekok.user_service.auth.application.dto.result;

import java.util.UUID;

public record SignupResult(
	UUID userId,
	String email,
	String username,
	String role,
	String accessToken
) {
}
