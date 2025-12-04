// auth/presentation/dto/request/CustomerSignupRequest.java
package com.tablekok.user_service.auth.presentation.dto.request;

import com.tablekok.user_service.auth.application.dto.CustomerSignupParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 고객 회원가입 요청 DTO
 * API 명세서: POST /v1/auth/signup/customer
 *
 * shipping-19man 스타일: Request → Param 변환 방식 적용
 * gashine20 피드백: record 방식 사용
 */
@Schema(description = "고객 회원가입 요청")
public record CustomerSignupRequest(

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "이메일 주소", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
	String email,

	@NotBlank(message = "이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글, 영문, 공백만 허용됩니다.")
	@Schema(description = "사용자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
	String username,

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함)", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
	String password,

	@NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 휴대폰번호 형식이 아닙니다. (01X + 8~9자리 숫자)")
	@Schema(description = "휴대폰번호 (하이픈 없이 입력)", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
	String phone
) {

	/**
	 * Request DTO → Application Layer Param 변환
	 * shipping-19man 스타일 적용
	 *
	 * @return CustomerSignupParam (Application Layer DTO)
	 */
	public CustomerSignupParam toParam() {
		return CustomerSignupParam.builder()
			.email(email)
			.username(username)
			.password(password)
			.phone(phone)
			.build();
	}

	/**
	 * API 요청 유효성 검증 추가 체크
	 * Validation 어노테이션 외 커스텀 검증
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

		// 휴대폰번호 길이 체크 (정확히 11자리)
		if (phone != null && phone.length() != 11) {
			return false;
		}

		return true;
	}

	/**
	 * 디버깅용 문자열 (비밀번호 마스킹)
	 */
	@Override
	public String toString() {
		return "CustomerSignupRequest{" +
			"email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", username='" + username + '\'' +
			", password='***'" +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			'}';
	}
}
