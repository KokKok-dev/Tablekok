package com.tablekok.user_service.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "로그인 응답")
public class LoginResponse {

	@Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;

	@Schema(description = "사용자 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID userId;

	@Schema(description = "사용자 이름", example = "홍길동")
	private String username;

	@Schema(description = "이메일", example = "user@example.com")
	private String email;

	@Schema(description = "휴대폰번호 (하이픈 제거)", example = "01012345678")
	private String phone;

	@Schema(description = "사용자 역할", example = "CUSTOMER")
	private String role;

	@Schema(description = "사업자번호 (사장님인 경우)", example = "123-45-67890")
	private String businessNumber;

	@Schema(description = "마지막 로그인 시간", example = "2024-01-15T10:30:00")
	private LocalDateTime lastLoginAt;

	@Schema(description = "총 로그인 횟수", example = "15")
	private Integer loginCount;

	@Schema(description = "계정 활성화 상태", example = "true")
	private Boolean isActive;

	@Builder
	public LoginResponse(String accessToken, UUID userId, String username, String email,
		String phone, String role, String businessNumber,
		LocalDateTime lastLoginAt, Integer loginCount, Boolean isActive) {
		this.accessToken = accessToken;
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.businessNumber = businessNumber;
		this.lastLoginAt = lastLoginAt;
		this.loginCount = loginCount;
		this.isActive = isActive;
	}
}
