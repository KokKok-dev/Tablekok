package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.exception.AuthDomainErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * User 도메인 검증 서비스
 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
 *
 * 주요 책임:
 * 1. User 생성 시 필드별 검증
 * 2. 비즈니스 규칙 검증
 * 3. 정규식 패턴 관리
 */
@Slf4j
@Component
public class UserValidator {

	// ========== 정규식 패턴 상수 ==========

	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private static final Pattern NAME_PATTERN =
		Pattern.compile("^[가-힣a-zA-Z\\s]+$");

	private static final Pattern PASSWORD_PATTERN =
		Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");

	private static final Pattern PHONE_PATTERN =
		Pattern.compile("^01[0-9]{8,9}$");

	// ========== 검증 메서드들 (AuthDomainErrorCode 사용) ==========

	/**
	 * 이메일 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 */
	public void validateEmail(String email) {
		log.debug("Validating email: {}", maskEmail(email));

		if (email == null || email.trim().isEmpty()) {
			throw new AppException(AuthDomainErrorCode.INVALID_EMAIL);
		}

		String normalized = normalizeEmail(email);

		if (!EMAIL_PATTERN.matcher(normalized).matches()) {
			throw new AppException(AuthDomainErrorCode.INVALID_EMAIL);
		}

		if (normalized.length() > 100) {
			throw new AppException(AuthDomainErrorCode.EMAIL_TOO_LONG);
		}

		log.debug("Email validation passed");
	}

	/**
	 * 이름 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 */
	public void validateName(String name) {
		log.debug("Validating name: {}", name);

		if (name == null || name.trim().isEmpty()) {
			throw new AppException(AuthDomainErrorCode.INVALID_NAME);
		}

		String trimmed = name.trim();

		if (trimmed.length() < 2 || trimmed.length() > 50) {
			throw new AppException(AuthDomainErrorCode.INVALID_NAME);
		}

		if (!NAME_PATTERN.matcher(trimmed).matches()) {
			throw new AppException(AuthDomainErrorCode.INVALID_NAME_FORMAT);
		}

		log.debug("Name validation passed");
	}

	/**
	 * 비밀번호 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 */
	public void validatePassword(String password) {
		log.debug("Validating password (length: {})", password != null ? password.length() : 0);

		if (password == null || password.trim().isEmpty()) {
			throw new AppException(AuthDomainErrorCode.INVALID_PASSWORD);
		}

		if (password.length() < 8 || password.length() > 20) {
			throw new AppException(AuthDomainErrorCode.INVALID_PASSWORD);
		}

		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new AppException(AuthDomainErrorCode.INVALID_PASSWORD_FORMAT);
		}

		log.debug("Password validation passed");
	}

	/**
	 * 휴대폰번호 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 */
	public void validatePhoneNumber(String phoneNumber) {
		log.debug("Validating phone number: {}", maskPhoneNumber(phoneNumber));

		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new AppException(AuthDomainErrorCode.INVALID_PHONE_NUMBER);
		}

		String normalized = normalizePhoneNumber(phoneNumber);

		if (!PHONE_PATTERN.matcher(normalized).matches()) {
			throw new AppException(AuthDomainErrorCode.INVALID_PHONE_FORMAT);
		}

		log.debug("Phone number validation passed");
	}

	/**
	 * 회원가입용 전체 검증
	 */
	public void validateUserCreation(String email, String name, String password, String phoneNumber) {
		log.debug("Starting user creation validation");

		validateEmail(email);
		validateName(name);
		validatePassword(password);
		validatePhoneNumber(phoneNumber);

		log.debug("All user creation validations passed");
	}

	// ========== 정규화 메서드들 ==========

	public String normalizeEmail(String email) {
		if (email == null) {
			return null;
		}
		return email.toLowerCase().trim();
	}

	public String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}
		return phoneNumber.replaceAll("-", "").replaceAll("\\s", "");
	}

	// ========== 보안 유틸리티 메서드들 ==========

	private String maskEmail(String email) {
		if (email == null || email.length() <= 3) {
			return "***";
		}
		int atIndex = email.indexOf('@');
		if (atIndex > 1) {
			return email.substring(0, 2) + "***" + email.substring(atIndex);
		}
		return email.substring(0, 1) + "***";
	}

	private String maskPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.length() <= 4) {
			return "***";
		}
		return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 3);
	}
}
