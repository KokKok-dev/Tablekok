package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.exception.AuthDomainErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 사업자번호 검증 도메인 서비스
 * ✅ gashine20 피드백 반영: AppException + AuthDomainErrorCode 사용
 *
 * 사업자번호 관련 모든 검증, 변환, 포맷팅 로직을 담당
 * - 형식 검증 (10자리 숫자)
 * - 체크섬 알고리즘 검증
 * - 정규화 (하이픈 제거)
 * - 포맷팅 (XXX-XX-XXXXX)
 * - 마스킹 (보안용)
 */
@Slf4j
@Component
public class BusinessNumberValidator {

	// ========== 상수 정의 ==========

	/**
	 * 사업자번호 형식 정규식 (10자리 숫자)
	 */
	private static final Pattern BUSINESS_NUMBER_PATTERN = Pattern.compile("^[0-9]{10}$");

	/**
	 * 사업자번호 포맷 정규식 (XXX-XX-XXXXX)
	 */
	private static final Pattern BUSINESS_NUMBER_FORMAT_PATTERN = Pattern.compile("^[0-9]{3}-[0-9]{2}-[0-9]{5}$");

	/**
	 * 체크섬 계산용 가중치 배열
	 * 국세청 사업자번호 체크섬 알고리즘 기준
	 */
	private static final int[] CHECK_SUM_WEIGHTS = {1, 3, 7, 1, 3, 7, 1, 3, 5};

	// ========== 공개 검증 메서드 (AppException 발생) ==========

	/**
	 * 사업자번호 필수값 및 형식 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 유효하지 않은 경우
	 */
	public void validateRequired(String businessNumber) {
		log.debug("Validating business number required: {}", maskForLog(businessNumber));

		if (businessNumber == null || businessNumber.trim().isEmpty()) {
			log.warn("Business number is required but was null or empty");
			throw new AppException(AuthDomainErrorCode.INVALID_BUSINESS_NUMBER);
		}
	}

	/**
	 * 사업자번호 형식 검증 (10자리 숫자)
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 형식이 올바르지 않은 경우
	 */
	public void validateFormat(String businessNumber) {
		log.debug("Validating business number format: {}", maskForLog(businessNumber));

		String normalized = normalize(businessNumber);

		if (!BUSINESS_NUMBER_PATTERN.matcher(normalized).matches()) {
			log.warn("Business number format validation failed: {}", maskForLog(businessNumber));
			throw new AppException(AuthDomainErrorCode.INVALID_BUSINESS_NUMBER_FORMAT);
		}

		log.debug("Business number format validation passed");
	}

	/**
	 * 사업자번호 체크섬 검증
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 체크섬이 올바르지 않은 경우
	 */
	public void validateChecksum(String businessNumber) {
		log.debug("Validating business number checksum: {}", maskForLog(businessNumber));

		String normalized = normalize(businessNumber);

		if (!isValidChecksum(normalized)) {
			log.warn("Business number checksum validation failed: {}", maskForLog(businessNumber));
			throw new AppException(AuthDomainErrorCode.INVALID_BUSINESS_NUMBER_CHECKSUM);
		}

		log.debug("Business number checksum validation passed");
	}

	/**
	 * 사업자번호 전체 검증 (필수값 + 형식 + 체크섬)
	 * ✅ gashine20 피드백 반영: AuthDomainErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 검증 실패 시
	 */
	public void validateAll(String businessNumber) {
		log.debug("Validating business number (all): {}", maskForLog(businessNumber));

		validateRequired(businessNumber);
		validateFormat(businessNumber);
		validateChecksum(businessNumber);

		log.debug("All business number validations passed");
	}

	// ========== Boolean 반환 검증 메서드 (기존 유지) ==========

	/**
	 * 사업자번호 전체 유효성 검증 (Boolean 반환)
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @return 유효하면 true, 아니면 false
	 */
	public boolean isValid(String businessNumber) {
		log.debug("Checking business number validity: {}", maskForLog(businessNumber));

		if (businessNumber == null || businessNumber.trim().isEmpty()) {
			log.debug("Business number is null or empty");
			return false;
		}

		String normalized = normalize(businessNumber);

		if (!isValidFormat(normalized)) {
			log.debug("Business number format validation failed: {}", maskForLog(businessNumber));
			return false;
		}

		boolean isValidChecksum = isValidChecksum(normalized);
		log.debug("Business number checksum validation result: {} for number: {}",
			isValidChecksum, maskForLog(businessNumber));

		return isValidChecksum;
	}

	/**
	 * 사업자번호 형식만 검증 (체크섬 제외)
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @return 형식이 올바르면 true
	 */
	public boolean isValidFormat(String businessNumber) {
		if (businessNumber == null) {
			return false;
		}

		String normalized = normalize(businessNumber);
		boolean isValid = BUSINESS_NUMBER_PATTERN.matcher(normalized).matches();

		log.debug("Business number format validation: {} for number: {}",
			isValid, maskForLog(businessNumber));

		return isValid;
	}

	/**
	 * 사업자번호 체크섬 검증 (Boolean 반환)
	 *
	 * @param businessNumber 검증할 사업자번호 (10자리 숫자)
	 * @return 체크섬이 올바르면 true
	 */
	public boolean isValidChecksum(String businessNumber) {
		if (businessNumber == null || businessNumber.length() != 10) {
			log.debug("Invalid business number length for checksum validation: {}",
				businessNumber != null ? businessNumber.length() : "null");
			return false;
		}

		try {
			int[] digits = new int[10];
			for (int i = 0; i < 10; i++) {
				digits[i] = Character.getNumericValue(businessNumber.charAt(i));
				if (digits[i] < 0 || digits[i] > 9) {
					log.debug("Invalid digit found at position {}: {}", i, businessNumber.charAt(i));
					return false;
				}
			}

			int sum = 0;
			for (int i = 0; i < 9; i++) {
				sum += digits[i] * CHECK_SUM_WEIGHTS[i];
			}

			sum += (digits[8] * 5) / 10;

			int checkDigit = (10 - (sum % 10)) % 10;
			boolean isValid = checkDigit == digits[9];

			log.debug("Checksum validation - calculated: {}, actual: {}, result: {} for number: {}",
				checkDigit, digits[9], isValid, maskForLog(businessNumber));

			return isValid;

		} catch (NumberFormatException e) {
			log.error("Error parsing business number for checksum validation: {}",
				maskForLog(businessNumber), e);
			return false;
		}
	}

	// ========== 변환 및 포맷팅 메서드 ==========

	/**
	 * 사업자번호 정규화 (하이픈 제거)
	 *
	 * @param businessNumber 원본 사업자번호
	 * @return 하이픈이 제거된 10자리 숫자 문자열
	 */
	public String normalize(String businessNumber) {
		if (businessNumber == null) {
			return null;
		}

		String normalized = businessNumber.replaceAll("-", "").trim();
		log.debug("Normalized business number from {} to {}",
			maskForLog(businessNumber), maskForLog(normalized));

		return normalized;
	}

	/**
	 * 사업자번호 포맷팅 (XXX-XX-XXXXX 형태)
	 *
	 * @param businessNumber 사업자번호 (10자리 숫자)
	 * @return 포맷팅된 사업자번호 (XXX-XX-XXXXX)
	 */
	public String format(String businessNumber) {
		if (businessNumber == null) {
			return null;
		}

		String normalized = normalize(businessNumber);

		if (normalized.length() != 10) {
			log.warn("Cannot format business number with invalid length: {}", normalized.length());
			return businessNumber;
		}

		String formatted = normalized.substring(0, 3) + "-" +
			normalized.substring(3, 5) + "-" +
			normalized.substring(5);

		log.debug("Formatted business number: {} -> {}",
			maskForLog(businessNumber), maskForLog(formatted));

		return formatted;
	}

	/**
	 * 사업자번호 마스킹 (보안용)
	 * XXX-**-****0 형태로 마스킹
	 *
	 * @param businessNumber 원본 사업자번호
	 * @return 마스킹된 사업자번호
	 */
	public String mask(String businessNumber) {
		if (businessNumber == null) {
			return "***-**-*****";
		}

		String normalized = normalize(businessNumber);

		if (normalized.length() != 10) {
			return "***-**-*****";
		}

		String masked = normalized.substring(0, 3) + "-**-****" + normalized.substring(9);

		log.debug("Masked business number for security");

		return masked;
	}

	// ========== 유틸리티 메서드 ==========

	/**
	 * 포맷된 사업자번호 형식인지 확인
	 *
	 * @param businessNumber 확인할 사업자번호
	 * @return XXX-XX-XXXXX 형태이면 true
	 */
	public boolean isFormattedNumber(String businessNumber) {
		if (businessNumber == null) {
			return false;
		}

		boolean isFormatted = BUSINESS_NUMBER_FORMAT_PATTERN.matcher(businessNumber).matches();
		log.debug("Business number format check: {} for number: {}",
			isFormatted, maskForLog(businessNumber));

		return isFormatted;
	}

	/**
	 * 사업자번호 지역 코드 추출 (첫 3자리)
	 *
	 * @param businessNumber 사업자번호
	 * @return 지역 코드 (첫 3자리)
	 */
	public String extractRegionCode(String businessNumber) {
		String normalized = normalize(businessNumber);

		if (normalized == null || normalized.length() < 3) {
			return null;
		}

		String regionCode = normalized.substring(0, 3);
		log.debug("Extracted region code: {} from business number: {}",
			regionCode, maskForLog(businessNumber));

		return regionCode;
	}

	/**
	 * 로깅용 사업자번호 마스킹
	 *
	 * @param businessNumber 원본 사업자번호
	 * @return 마스킹된 사업자번호 (로깅용)
	 */
	private String maskForLog(String businessNumber) {
		if (businessNumber == null) {
			return "null";
		}

		if (businessNumber.length() <= 4) {
			return "***";
		}

		return businessNumber.substring(0, 2) + "***" +
			businessNumber.substring(businessNumber.length() - 2);
	}
}
