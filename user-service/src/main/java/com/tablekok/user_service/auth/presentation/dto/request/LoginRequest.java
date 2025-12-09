package com.tablekok.user_service.auth.presentation.dto.request;

import com.tablekok.user_service.auth.application.dto.command.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 로그인 요청 DTO
 * API 명세서: POST /v1/auth/login
 *
 * gashine20 피드백 반영: toParam() → toCommand() 메서드명 변경
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 * 순수 Validation만 담당 - 나머지 로직은 제거
 */
@Schema(description = "로그인 요청")
public record LoginRequest(

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "로그인 이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	String email,

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Schema(description = "로그인 비밀번호", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
	String password
) {

	/**
	 * Request DTO → Application Layer Command 변환
	 * ✅ gashine20 피드백 반영: toParam() → toCommand() 메서드명 변경
	 *
	 * @return LoginCommand (Application Layer DTO)
	 */
	public LoginCommand toCommand() {  // ✅ toParam() → toCommand() 변경
		return LoginCommand.builder()
			.email(email)
			.password(password)
			.build();
	}
}
