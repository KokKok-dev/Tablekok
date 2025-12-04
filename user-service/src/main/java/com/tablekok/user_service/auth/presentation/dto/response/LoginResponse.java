// auth/presentation/dto/response/LoginResponse.java
package com.tablekok.user_service.auth.presentation.dto.response;

import com.tablekok.user_service.auth.application.dto.LoginResult;
import com.tablekok.user_service.auth.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 로그인 응답 DTO
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 *
 * API 명세서 응답 형식 정확 매핑
 * 로그인 통계 정보 포함
 */
@Schema(description = "로그인 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public record LoginResponse(

	@Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	String accessToken,

	@Schema(description = "사용자 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
	UUID userId,

	@Schema(description = "사용자 이름", example = "홍길동")
	String username,

	@Schema(description = "이메일 주소", example = "user@example.com")
	String email,

	@Schema(description = "휴대폰번호 (하이픈 제거)", example = "01012345678")
	String phone,

	@Schema(description = "사용자 역할", example = "CUSTOMER", allowableValues = {"CUSTOMER", "OWNER", "MASTER"})
	String role,

	@Schema(description = "사업자번호 (Owner인 경우만)", example = "1234567890")
	String businessNumber,

	@Schema(description = "마지막 로그인 시간", example = "2024-01-15T10:30:00")
	LocalDateTime lastLoginAt,

	@Schema(description = "총 로그인 횟수", example = "15")
	Integer loginCount,

	@Schema(description = "계정 활성 상태", example = "true")
	Boolean isActive
) {

	/**
	 * Application Layer Result → Presentation Response 변환
	 *
	 * @param loginResult Application Layer 결과
	 * @return LoginResponse (Presentation Layer DTO)
	 */
	public static LoginResponse from(LoginResult loginResult) {
		return new LoginResponse(
			loginResult.accessToken(),
			loginResult.userId(),
			loginResult.username(),
			loginResult.email(),
			loginResult.phone(),
			loginResult.role().name(),  // Enum을 String으로 변환
			loginResult.businessNumber(),  // Owner인 경우만 존재
			loginResult.lastLoginAt(),
			loginResult.loginCount(),
			loginResult.isActive()
		);
	}

	/**
	 * Customer 로그인 응답 생성
	 * 사업자번호 없음을 명시
	 *
	 * @param loginResult Application Layer 결과
	 * @return Customer 로그인 응답
	 */
	public static LoginResponse fromCustomer(LoginResult loginResult) {
		return new LoginResponse(
			loginResult.accessToken(),
			loginResult.userId(),
			loginResult.username(),
			loginResult.email(),
			loginResult.phone(),
			UserRole.CUSTOMER.name(),
			null,  // Customer는 사업자번호 없음
			loginResult.lastLoginAt(),
			loginResult.loginCount(),
			loginResult.isActive()
		);
	}

	/**
	 * Owner 로그인 응답 생성
	 * 사업자번호 포함
	 *
	 * @param loginResult Application Layer 결과
	 * @return Owner 로그인 응답
	 */
	public static LoginResponse fromOwner(LoginResult loginResult) {
		return new LoginResponse(
			loginResult.accessToken(),
			loginResult.userId(),
			loginResult.username(),
			loginResult.email(),
			loginResult.phone(),
			UserRole.OWNER.name(),
			loginResult.businessNumber(),  // Owner 사업자번호 포함
			loginResult.lastLoginAt(),
			loginResult.loginCount(),
			loginResult.isActive()
		);
	}

	/**
	 * Master 로그인 응답 생성
	 * 사업자번호 없음
	 *
	 * @param loginResult Application Layer 결과
	 * @return Master 로그인 응답
	 */
	public static LoginResponse fromMaster(LoginResult loginResult) {
		return new LoginResponse(
			loginResult.accessToken(),
			loginResult.userId(),
			loginResult.username(),
			loginResult.email(),
			loginResult.phone(),
			UserRole.MASTER.name(),
			null,  // Master는 사업자번호 없음
			loginResult.lastLoginAt(),
			loginResult.loginCount(),
			loginResult.isActive()
		);
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isCustomer() {
		return "CUSTOMER".equals(role);
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isOwner() {
		return "OWNER".equals(role);
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isMaster() {
		return "MASTER".equals(role);
	}

	/**
	 * 사업자번호 존재 여부 확인
	 */
	public boolean hasBusinessNumber() {
		return businessNumber != null && !businessNumber.trim().isEmpty();
	}

	/**
	 * 계정 활성 상태 확인
	 */
	public boolean isAccountActive() {
		return Boolean.TRUE.equals(isActive);
	}

	/**
	 * 첫 로그인 여부 확인
	 */
	public boolean isFirstLogin() {
		return loginCount != null && loginCount == 1;
	}

	/**
	 * 마스킹된 액세스 토큰 반환 (로깅용)
	 */
	public String getMaskedAccessToken() {
		if (accessToken == null || accessToken.length() <= 10) {
			return "***";
		}

		return accessToken.substring(0, 10) + "..." +
			accessToken.substring(accessToken.length() - 4);
	}

	/**
	 * 디버깅용 문자열 (민감정보 마스킹)
	 */
	@Override
	public String toString() {
		return "LoginResponse{" +
			"accessToken='" + getMaskedAccessToken() + '\'' +
			", userId=" + userId +
			", username='" + username + '\'' +
			", email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			", role='" + role + '\'' +
			", businessNumber='" + (businessNumber != null ? businessNumber.substring(0, Math.min(3, businessNumber.length())) + "***" : "null") + '\'' +
			", lastLoginAt=" + lastLoginAt +
			", loginCount=" + loginCount +
			", isActive=" + isActive +
			'}';
	}
}
