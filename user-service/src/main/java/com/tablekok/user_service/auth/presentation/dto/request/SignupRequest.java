package com.tablekok.user_service.auth.presentation.dto.request;

import com.tablekok.user_service.auth.application.dto.command.SignupCommand;
import jakarta.validation.constraints.*;

public record SignupRequest(
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다.")
	String password,

	@NotBlank(message = "이름은 필수입니다.")
	@Size(min = 1, max = 50, message = "이름은 1-50자 사이여야 합니다.")
	String username,

	@NotBlank(message = "휴대폰번호는 필수입니다.")
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 휴대폰번호 형식이어야 합니다.")
	String phoneNumber,

	String businessNumber // 있으면 OWNER 없으면 CUSTOMER
) {
	public SignupCommand toCommand() {
		return new SignupCommand(
			email,
			password,
			username,
			phoneNumber,
			businessNumber
		);
	}
}
