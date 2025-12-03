package com.tablekok.user_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "로그인 요청")
public class LoginRequest {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "로그인 이메일", example = "user@example.com", required = true)
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Schema(description = "로그인 비밀번호", example = "Password123!", required = true)
	private String password;

	// 생성자
	public LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	// 이메일 정규화 (소문자 변환, 공백 제거)
	public String getNormalizedEmail() {
		return email.toLowerCase().trim();
	}

	// 보안을 위한 toString 오버라이드 (비밀번호 마스킹)
	@Override
	public String toString() {
		return "LoginRequest{" +
			"email='" + email + '\'' +
			", password='***'" +
			'}';
	}
}
