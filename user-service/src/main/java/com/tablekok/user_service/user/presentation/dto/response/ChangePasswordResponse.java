package com.tablekok.user_service.user.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChangePasswordResponse(
	boolean status,
	String message,
	LocalDateTime changedAt
) {
	public static ChangePasswordResponse success() {
		return ChangePasswordResponse.builder()
			.status(true)
			.message("비밀번호가 성공적으로 변경되었습니다.")
			.changedAt(LocalDateTime.now())
			.build();
	}
}
