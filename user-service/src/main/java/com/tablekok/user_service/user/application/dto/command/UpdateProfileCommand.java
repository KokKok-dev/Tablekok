package com.tablekok.user_service.user.application.dto.command;

public record UpdateProfileCommand(
	String phoneNumber,
	String businessNumber
) {
}
