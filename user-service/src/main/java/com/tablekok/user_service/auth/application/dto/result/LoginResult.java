package com.tablekok.user_service.auth.application.dto.result;

import java.util.UUID;

public record LoginResult(
	UUID userId,
	String email,
	String username,
	String role,
	String accessToken,
	String refreshToken
) {
}
