package com.tablekok.user_service.auth.presentation.dto.request;

import com.tablekok.user_service.auth.application.dto.command.OwnerSignupCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 사장님 회원가입 요청 DTO
 * API 명세서: POST /v1/auth/signup/owner
 *
 * Customer 회원가입 + 사업자번호 검증 추가
 * 순수 Validation만 담당 - 나머지 로직은 제거
 */
@Schema(description = "사장님 회원가입 요청")
public record OwnerSignupRequest(

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "이메일 주소", example = "owner@restaurant.com", requiredMode = Schema.RequiredMode.REQUIRED)
	String email,

	@NotBlank(message = "이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글, 영문, 공백만 허용됩니다.")
	@Schema(description = "사업자 이름", example = "김사장", requiredMode = Schema.RequiredMode.REQUIRED)
	String username,

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함)", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
	String password,

	@NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 휴대폰번호 형식이 아닙니다. (01X + 8~9자리 숫자)")
	@Schema(description = "휴대폰번호 (하이픈 없이 입력)", example = "01087654321", requiredMode = Schema.RequiredMode.REQUIRED)
	String phone,

	@NotBlank(message = "사업자번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "사업자번호는 XXX-XX-XXXXX 형태로 입력해주세요.")
	@Schema(description = "사업자번호 (XXX-XX-XXXXX 형태)", example = "123-45-67890", requiredMode = Schema.RequiredMode.REQUIRED)
	String businessNumber
) {

	/**
	 * Request DTO → Application Layer Param 변환
	 * shipping-19man 스타일 적용
	 *
	 * @return OwnerSignupParam (Application Layer DTO)
	 */
	public OwnerSignupCommand toParam() {
		return OwnerSignupCommand.builder()
			.email(email)
			.username(username)
			.password(password)
			.phone(phone)
			.businessNumber(businessNumber)
			.build();
	}

	// ❌ 제거된 메서드들:
	// - isValidRequest() → Service에서 처리
	// - getNormalizedBusinessNumber() → Service에서 처리
	// - toString() 오버라이드 → record 기본 toString 사용
}
