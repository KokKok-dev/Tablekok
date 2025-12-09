package com.tablekok.user_service.auth.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원가입 결과 Application Layer DTO (Result)
 * 코드 컨벤션: Create<Entity>Result 패턴 적용
 *
 * Customer/Owner 회원가입 공통 결과 DTO
 * JWT 토큰과 기본 사용자 정보 포함
 */
@Builder
public record SignupResult(
	String accessToken,
	UUID userId,
	String username,
	String email,
	String phone,
	UserRole role,
	String businessNumber,  // Owner인 경우만 존재
	LocalDateTime signupAt
) {

	/**
	 * Customer 회원가입 결과 생성 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 생성된 Customer User 엔티티
	 * @return Customer 회원가입 결과
	 */
	public static SignupResult fromCustomer(String accessToken, User user) {
		return SignupResult.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(null)  // Customer는 사업자번호 없음
			.signupAt(user.getCreatedAt())
			.build();
	}

	/**
	 * Owner 회원가입 결과 생성 팩토리 메서드
	 *
	 * @param accessToken JWT 액세스 토큰
	 * @param user 생성된 Owner User 엔티티
	 * @return Owner 회원가입 결과
	 */
	public static SignupResult fromOwner(String accessToken, User user) {
		return SignupResult.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(user.getBusinessNumber())  // Owner 사업자번호 포함
			.signupAt(user.getCreatedAt())
			.build();
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
	 * 사업자번호 존재 여부 확인
	 */
	public boolean hasBusinessNumber() {
		return businessNumber != null && !businessNumber.trim().isEmpty();
	}
}
