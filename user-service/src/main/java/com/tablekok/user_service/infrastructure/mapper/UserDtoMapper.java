package com.tablekok.user_service.infrastructure.mapper;

import com.tablekok.user_service.domain.entity.Owner;
import com.tablekok.user_service.domain.entity.User;
import com.tablekok.user_service.domain.enums.UserRole;
import com.tablekok.user_service.presentation.dto.request.CustomerSignupRequest;
import com.tablekok.user_service.presentation.dto.request.OwnerSignupRequest;
import com.tablekok.user_service.presentation.dto.response.LoginResponse;
import com.tablekok.user_service.presentation.dto.response.SignupResponse;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

	// ========== Request DTO → Entity 변환 ==========

	/**
	 * CustomerSignupRequest → User Entity 변환
	 */
	public User toEntity(CustomerSignupRequest request, String encodedPassword) {
		return User.builder()
			.email(normalizeEmail(request.getEmail()))
			.name(request.getUsername())
			.password(encodedPassword)
			.phoneNumber(normalizePhoneNumber(request.getPhone()))
			.role(UserRole.CUSTOMER)
			.build();
	}

	/**
	 * OwnerSignupRequest → User Entity 변환
	 */
	public User toUserEntity(OwnerSignupRequest request, String encodedPassword) {
		return User.builder()
			.email(normalizeEmail(request.getEmail()))
			.name(request.getUsername())
			.password(encodedPassword)
			.phoneNumber(normalizePhoneNumber(request.getPhone()))
			.role(UserRole.OWNER)
			.build();
	}

	/**
	 * OwnerSignupRequest → Owner Entity 변환
	 */
	public Owner toOwnerEntity(OwnerSignupRequest request, User user) {
		return Owner.builder()
			.user(user)
			.businessNumber(request.getBusinessNumber())
			.build();
	}

	// ========== Entity → Response DTO 변환 ==========

	/**
	 * Customer 회원가입 응답 생성
	 */
	public SignupResponse toCustomerSignupResponse(String accessToken, User user) {
		return SignupResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.build();
	}

	/**
	 * Owner 회원가입 응답 생성
	 */
	public SignupResponse toOwnerSignupResponse(String accessToken, User user) {
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

	/**
	 * Customer 로그인 응답 생성
	 */
	public LoginResponse toCustomerLoginResponse(String accessToken, User user) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.build();
	}

	/**
	 * Owner 로그인 응답 생성
	 */
	public LoginResponse toOwnerLoginResponse(String accessToken, User user) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.businessNumber(user.getBusinessNumber())
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.build();
	}

	/**
	 * Master 로그인 응답 생성
	 */
	public LoginResponse toMasterLoginResponse(String accessToken, User user) {
		return LoginResponse.builder()
			.accessToken(accessToken)
			.userId(user.getUserId())
			.username(user.getName())
			.email(user.getEmail())
			.phone(user.getPhoneNumber())
			.role(user.getRole().getValue())
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.isActive(user.getIsActive())
			.build();
	}

	/**
	 * 통합 로그인 응답 생성 (역할 자동 판별)
	 */
	public LoginResponse toLoginResponse(String accessToken, User user) {
		switch (user.getRole()) {
			case CUSTOMER:
				return toCustomerLoginResponse(accessToken, user);
			case OWNER:
				return toOwnerLoginResponse(accessToken, user);
			case MASTER:
				return toMasterLoginResponse(accessToken, user);
			default:
				throw new IllegalArgumentException("지원하지 않는 사용자 역할입니다: " + user.getRole());
		}
	}

	// ========== 내부 유틸리티 메서드 ==========

	/**
	 * 이메일 정규화 (소문자 변환, 공백 제거)
	 */
	private String normalizeEmail(String email) {
		if (email == null) {
			return null;
		}
		return email.toLowerCase().trim();
	}

	/**
	 * 휴대폰번호 정규화 (하이픈 제거)
	 */
	private String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}
		return phoneNumber.replaceAll("-", "");
	}
}
