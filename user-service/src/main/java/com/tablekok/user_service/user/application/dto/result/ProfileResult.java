package com.tablekok.user_service.user.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;
public record ProfileResult (
	UUID userId,
	String email,
	String username,
	String phoneNumber,
	String role,
	String businessNumber,
	LocalDateTime createdAt
) {
}
