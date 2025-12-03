package com.tablekok.user_service.presentation.dto.request;

import com.tablekok.user_service.domain.entity.Owner;
import com.tablekok.user_service.domain.entity.User;
import com.tablekok.user_service.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "사장님 회원가입 요청")
public class OwnerSignupRequest {

	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	@Schema(description = "이메일", example = "owner@restaurant.com", required = true)
	private String email;

	@NotBlank(message = "이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글, 영문, 공백만 허용됩니다.")
	@Schema(description = "사업자 이름", example = "김사장", required = true)
	private String username;

	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8-20자)", example = "Password123!", required = true)
	private String password;

	@NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 휴대폰번호 형식이 아닙니다. (01X + 8-9자리)")
	@Schema(description = "휴대폰번호 (하이픈 없이)", example = "01087654321", required = true)
	private String phone;

	@NotBlank(message = "사업자번호는 필수 입력 값입니다.")
	@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "올바른 사업자번호 형식이 아닙니다. (XXX-XX-XXXXX)")
	@Schema(description = "사업자번호 (XXX-XX-XXXXX 형태)", example = "123-45-67890", required = true)
	private String businessNumber;

	// 생성자
	public OwnerSignupRequest(String email, String username, String password, String phone, String businessNumber) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
		this.businessNumber = businessNumber;
	}

	// User 엔티티로 변환
	public User toUserEntity(String encodedPassword) {
		return User.builder()
			.email(this.email)
			.name(this.username)
			.password(encodedPassword)
			.phoneNumber(this.phone)
			.role(UserRole.OWNER)
			.build();
	}

	// Owner 엔티티로 변환
	public Owner toOwnerEntity(User user) {
		return Owner.builder()
			.user(user)
			.businessNumber(this.businessNumber)
			.build();
	}

	// 휴대폰번호 정규화 (하이픈 제거)
	public String getNormalizedPhoneNumber() {
		return phone.replaceAll("-", "");
	}

	// 이메일 정규화 (소문자 변환)
	public String getNormalizedEmail() {
		return email.toLowerCase().trim();
	}

	// 사업자번호 정규화 (하이픈 제거)
	public String getNormalizedBusinessNumber() {
		return businessNumber.replaceAll("-", "");
	}

	// 사업자번호 유효성 검증
	public boolean isValidBusinessNumber() {
		if (businessNumber == null || !businessNumber.matches("^\\d{3}-\\d{2}-\\d{5}$")) {
			return false;
		}

		// 실제 사업자번호 체크섬 검증 로직
		String numbers = businessNumber.replaceAll("-", "");
		int[] weights = {1, 3, 7, 1, 3, 7, 1, 3, 5};
		int sum = 0;

		for (int i = 0; i < 9; i++) {
			sum += Character.getNumericValue(numbers.charAt(i)) * weights[i];
		}

		sum += (Character.getNumericValue(numbers.charAt(8)) * 5) / 10;
		int checkDigit = (10 - (sum % 10)) % 10;

		return checkDigit == Character.getNumericValue(numbers.charAt(9));
	}
}
