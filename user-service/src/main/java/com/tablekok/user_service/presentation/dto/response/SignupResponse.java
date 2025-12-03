package com.tablekok.user_service.presentation.dto.response;

import com.tablekok.user_service.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원가입 응답")
public class SignupResponse {

	@Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;

	@Schema(description = "사용자 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID userId;

	@Schema(description = "사용자 이름", example = "홍길동")
	private String username;

	@Schema(description = "이메일", example = "customer@example.com")
	private String email;

	@Schema(description = "휴대폰번호 (하이픈 제거)", example = "01012345678")
	private String phone;

	@Schema(description = "사용자 역할", example = "CUSTOMER")
	private String role;

	@Schema(description = "사업자번호 (사장님인 경우)", example = "123-45-67890")
	private String businessNumber;

	@Builder
	public SignupResponse(String accessToken, UUID userId, String username, String email,
		String phone, String role, String businessNumber) {
		this.accessToken = accessToken;
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.businessNumber = businessNumber;
	}

	// Customer 회원가입 응답 생성
	public static SignupResponse fromCustomer(String accessToken, User user) {
		return SignupResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.build();
	}

	// Owner 회원가입 응답 생성
	public static SignupResponse fromOwner(String accessToken, User user) {
		return SignupResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.businessNumber(user.getBusinessNumber())
			.build();
	}
}
