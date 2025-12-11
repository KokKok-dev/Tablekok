package com.tablekok.user_service.auth.application.dto.command;

public record SignupCommand(
	String email,
	String password,
	String username,
	String phoneNumber,
	String businessNumber
) {
	public boolean hasBusinessNumber() {
		return businessNumber != null && !businessNumber.isBlank();
	}
}
