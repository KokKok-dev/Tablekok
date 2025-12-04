// auth/presentation/dto/request/LoginRequest.java
package com.tablekok.user_service.auth.presentation.dto.request;

import com.tablekok.user_service.auth.application.dto.LoginParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 로그인 요청 DTO
 * API 명세서: POST /v1/auth/login
 *
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
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
	 * Request DTO → Application Layer Param 변환
	 * shipping-19man 스타일 적용
	 *
	 * @return LoginParam (Application Layer DTO)
	 */
	public LoginParam toParam() {
		return LoginParam.builder()
			.email(email)
			.password(password)
			.build();
	}

	/**
	 * API 요청 유효성 검증 추가 체크
	 *
	 * @return 유효하면 true
	 */
	public boolean isValidRequest() {
		// 이메일 도메인 기본 체크
		if (email != null && email.contains("@")) {
			String domain = email.substring(email.lastIndexOf("@") + 1);
			if (domain.length() < 2) {
				return false;
			}
		}

		// 비밀번호 기본 공백 체크
		if (password != null && password.trim().isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * 이메일 정규화 (소문자 변환 + 공백 제거)
	 *
	 * @return 정규화된 이메일
	 */
	public String getNormalizedEmail() {
		return email != null ? email.toLowerCase().trim() : null;
	}

	/**
	 * 디버깅용 문자열 (비밀번호 마스킹)
	 */
	@Override
	public String toString() {
		return "LoginRequest{" +
			"email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", password='***'" +
			'}';
	}
}
