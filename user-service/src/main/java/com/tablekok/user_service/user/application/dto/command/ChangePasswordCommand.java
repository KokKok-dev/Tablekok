package com.tablekok.user_service.user.application.dto.command;

public record ChangePasswordCommand(
	String currentPassword,
	String newPassword,
	String confirmPassword
) {
}
