// auth/application/dto/LoginResult.java
package com.tablekok.user_service.auth.application.dto;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.enums.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 로그인 결과 Application Layer DTO (Result)
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 *
 * JWT 토큰과 사용자 정보, 로그인 통계 포함
 */
@Builder
public record LoginResult(
	String accessToken,
	UUID userId,
	String username,
	String email,
	String phone,
	UserRole role,
	String businessNumber,      // Owner인 경우만 존재
	LocalDateTime lastLoginAt,
	Integer loginCount,
	Boolean isActive,
	LocalDateTime loginAt       // 현재 로그인 시각
) {

	/**
	 * Customer 로그인 결과 생성 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 로그인한 Customer User 엔티티
	 * @return Customer 로그인 결과
	 */
	public static LoginResult fromCustomer(String accessToken, User user) {
		return LoginResult.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(null)  // Customer는 사업자번호 없음
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.loginAt(LocalDateTime.now())
			.build();
	}

	/**
	 * Owner 로그인 결과 생성 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 로그인한 Owner User 엔티티
	 * @return Owner 로그인 결과
	 */
	public static LoginResult fromOwner(String accessToken, User user) {
		return LoginResult.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(user.getBusinessNumber())  // Owner 사업자번호 포함
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.loginAt(LocalDateTime.now())
			.build();
	}

	/**
	 * Master 로그인 결과 생성 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 로그인한 Master User 엔티티
	 * @return Master 로그인 결과
	 */
	public static LoginResult fromMaster(String accessToken, User user) {
		return LoginResult.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(null)  // Master는 사업자번호 없음
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.loginAt(LocalDateTime.now())
			.build();
	}

	/**
	 * 역할에 따른 자동 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 로그인한 User 엔티티
	 * @return 역할에 맞는 로그인 결과
	 */
	public static LoginResult from(String accessToken, User user) {
		return switch (user.getRole()) {
			case CUSTOMER -> fromCustomer(accessToken, user);
			case OWNER -> fromOwner(accessToken, user);
			case MASTER -> fromMaster(accessToken, user);
		};
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isCustomer() {
		return UserRole.CUSTOMER == role;
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isOwner() {
		return UserRole.OWNER == role;
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isMaster() {
		return UserRole.MASTER == role;
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
		return "LoginResult{" +
			"accessToken='" + getMaskedAccessToken() + '\'' +
			", userId=" + userId +
			", username='" + username + '\'' +
			", email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			", role=" + role +
			", businessNumber='" + (businessNumber != null ? businessNumber.substring(0, Math.min(3, businessNumber.length())) + "***" : "null") + '\'' +
			", lastLoginAt=" + lastLoginAt +
			", loginCount=" + loginCount +
			", isActive=" + isActive +
			", loginAt=" + loginAt +
			'}';
	}
}
