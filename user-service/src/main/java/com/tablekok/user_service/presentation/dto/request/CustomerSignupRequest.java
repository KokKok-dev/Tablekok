package com.tablekok.user_service.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "고객 회원가입 요청")
public class CustomerSignupRequest {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "이메일", example = "customer@example.com", required = true)
	private String email;

	@NotBlank(message = "이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글, 영문, 공백만 허용됩니다.")
	@Schema(description = "사용자 이름", example = "홍길동", required = true)
	private String username;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8-20자)", example = "Password123!", required = true)
	private String password;

	@NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 휴대폰번호 형식이 아닙니다. (01X + 8-9자리)")
	@Schema(description = "휴대폰번호 (하이픈 없이)", example = "01012345678", required = true)
	private String phone;

	// 생성자
	public CustomerSignupRequest(String email, String username, String password, String phone) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
	}
}
