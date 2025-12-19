package com.tablekok.user_service.auth.application.dto.command;

public record LoginCommand(
	String email,
	String password
) {
}
