package com.tablekok.user_service.user.presentation.dto.request;

import com.tablekok.user_service.user.application.dto.command.UpdateProfileCommand;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
	String phoneNumber,

	@Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자번호 형식이 올바르지 않습니다.")
	String businessNumber
) {
	public UpdateProfileCommand toCommand() {
		return new UpdateProfileCommand(phoneNumber, businessNumber);
	}
}
