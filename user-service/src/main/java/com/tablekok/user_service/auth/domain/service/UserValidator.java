package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.exception.AuthErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * User 도메인 검증 서비스
 *
 * gashine20 피드백 반영: IllegalArgumentException → AppException 변경
 * AuthErrorCode 사용으로 명확한 에러 구분
 * Common의 GlobalExceptionHandler에서 자동으로 처리됩니다.
 *
 * 주요 책임:
 * 1. User 생성 시 필드별 검증
 * 2. 비즈니스 규칙 검증
 * 3. 정규식 패턴 관리
 *
 * Entity는 순수한 도메인 모델에만 집중
 * 검증 로직은 Domain Service에서 담당
 */
@Slf4j
@Component
public class UserValidator {

	// ========== 정규식 패턴 상수 ==========

	/**
	 * 이메일 검증 정규식
	 */
	private static final Pattern EMAIL_PATTERN =
		Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	/**
	 * 이름 검증 정규식 (한글, 영문, 공백만 허용)
	 */
	private static final Pattern NAME_PATTERN =
		Pattern.compile("^[가-힣a-zA-Z\\s]+$");

	/**
	 * 비밀번호 검증 정규식 (영문, 숫자, 특수문자 포함)
	 */
	private static final Pattern PASSWORD_PATTERN =
		Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");

	/**
	 * 휴대폰번호 검증 정규식 (01X + 8~9자리 숫자)
	 */
	private static final Pattern PHONE_PATTERN =
		Pattern.compile("^01[0-9]{8,9}$");

	// ========== 검증 메서드들 (AuthErrorCode 사용) ==========

	/**
	 * 이메일 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param email 검증할 이메일
	 * @throws AppException 유효하지 않은 이메일인 경우
	 */
	public void validateEmail(String email) {
		log.debug("Validating email: {}", maskEmail(email));

		if (email == null || email.trim().isEmpty()) {
			throw new AppException(AuthErrorCode.INVALID_EMAIL);
		}

		String normalized = normalizeEmail(email);

		if (!EMAIL_PATTERN.matcher(normalized).matches()) {
			throw new AppException(AuthErrorCode.INVALID_EMAIL);
		}

		if (normalized.length() > 100) {
			throw new AppException(AuthErrorCode.EMAIL_TOO_LONG);
		}

		log.debug("Email validation passed");
	}

	/**
	 * 이름 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param name 검증할 이름
	 * @throws AppException 유효하지 않은 이름인 경우
	 */
	public void validateName(String name) {
		log.debug("Validating name: {}", name);

		if (name == null || name.trim().isEmpty()) {
			throw new AppException(AuthErrorCode.INVALID_NAME);
		}

		String trimmed = name.trim();

		if (trimmed.length() < 2 || trimmed.length() > 50) {
			throw new AppException(AuthErrorCode.INVALID_NAME);
		}

		if (!NAME_PATTERN.matcher(trimmed).matches()) {
			throw new AppException(AuthErrorCode.INVALID_NAME_FORMAT);
		}

		log.debug("Name validation passed");
	}

	/**
	 * 비밀번호 도메인 검증 (원본 비밀번호용)
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param password 검증할 원본 비밀번호
	 * @throws AppException 유효하지 않은 비밀번호인 경우
	 */
	public void validatePassword(String password) {
		log.debug("Validating password (length: {})", password != null ? password.length() : 0);

		if (password == null || password.trim().isEmpty()) {
			throw new AppException(AuthErrorCode.INVALID_PASSWORD);
		}

		if (password.length() < 8 || password.length() > 20) {
			throw new AppException(AuthErrorCode.INVALID_PASSWORD);
		}

		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new AppException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
		}

		log.debug("Password validation passed");
	}

	/**
	 * 휴대폰번호 도메인 검증
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param phoneNumber 검증할 휴대폰번호
	 * @throws AppException 유효하지 않은 휴대폰번호인 경우
	 */
	public void validatePhoneNumber(String phoneNumber) {
		log.debug("Validating phone number: {}", maskPhoneNumber(phoneNumber));

		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new AppException(AuthErrorCode.INVALID_PHONE_NUMBER);
		}

		String normalized = normalizePhoneNumber(phoneNumber);

		if (!PHONE_PATTERN.matcher(normalized).matches()) {
			throw new AppException(AuthErrorCode.INVALID_PHONE_FORMAT);
		}

		log.debug("Phone number validation passed");
	}

	/**
	 * 회원가입용 전체 검증
	 *
	 * @param email 이메일
	 * @param name 이름
	 * @param password 비밀번호
	 * @param phoneNumber 휴대폰번호
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

	/**
	 * 이메일 정규화
	 */
	public String normalizeEmail(String email) {
		if (email == null) {
			return null;
		}
		return email.toLowerCase().trim();
	}

	/**
	 * 휴대폰번호 정규화
	 */
	public String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}
		return phoneNumber.replaceAll("-", "").replaceAll("\\s", "");
	}

	// ========== 보안 유틸리티 메서드들 ==========

	/**
	 * 이메일 마스킹 (로깅용)
	 */
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

	/**
	 * 휴대폰번호 마스킹 (로깅용)
	 */
	private String maskPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.length() <= 4) {
			return "***";
		}
		return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 3);
	}
}
