package com.tablekok.user_service.user.presentation.dto.request;

import com.tablekok.user_service.user.application.dto.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
	@NotBlank(message = "현재 비밀번호는 필수입니다.")
	String currentPassword,

	@NotBlank(message = "새 비밀번호는 필수입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
	String newPassword,

	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	String confirmPassword
) {
	public ChangePasswordCommand toCommand() {
		return new ChangePasswordCommand(currentPassword, newPassword, confirmPassword);
	}
}
