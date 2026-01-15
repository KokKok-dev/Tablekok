package com.tablekok.user_service.auth.presentation.dto.response;

import com.tablekok.user_service.auth.application.dto.result.RefreshTokenResult;

public record RefreshTokenResponse(
	String accessToken
) {
	public static RefreshTokenResponse from(RefreshTokenResult result) {
		return new RefreshTokenResponse(result.accessToken());
	}
}
