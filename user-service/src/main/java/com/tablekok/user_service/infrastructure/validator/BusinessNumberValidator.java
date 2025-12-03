package com.tablekok.user_service.infrastructure.validator;

import org.springframework.stereotype.Component;

@Component
public class BusinessNumberValidator {

	/**
	 * 사업자번호 유효성 검증
	 * - 형식 검증: XXX-XX-XXXXX 패턴
	 * - 체크섬 검증: 실제 사업자번호 알고리즘 적용
	 */
	public boolean isValid(String businessNumber) {
		if (businessNumber == null || businessNumber.trim().isEmpty()) {
			return false;
		}

		// 1. 형식 검증
		if (!businessNumber.matches("^\\d{3}-\\d{2}-\\d{5}$")) {
			return false;
		}

		// 2. 체크섬 검증
		return isValidChecksum(businessNumber);
	}

	/**
	 * 사업자번호 체크섬 검증
	 */
	private boolean isValidChecksum(String businessNumber) {
		try {
			// 하이픈 제거
			String numbers = businessNumber.replaceAll("-", "");

			// 가중치 배열
			int[] weights = {1, 3, 7, 1, 3, 7, 1, 3, 5};
			int sum = 0;

			// 첫 9자리에 가중치 적용
			for (int i = 0; i < 9; i++) {
				int digit = Character.getNumericValue(numbers.charAt(i));
				sum += digit * weights[i];
			}

			// 9번째 자리 특별 처리
			int ninthDigit = Character.getNumericValue(numbers.charAt(8));
			sum += (ninthDigit * 5) / 10;

			// 체크 디지트 계산
			int checkDigit = (10 - (sum % 10)) % 10;

			// 10번째 자리와 비교
			int lastDigit = Character.getNumericValue(numbers.charAt(9));

			return checkDigit == lastDigit;

		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * 사업자번호 정규화 (하이픈 제거)
	 */
	public String normalize(String businessNumber) {
		if (businessNumber == null) {
			return null;
		}
		return businessNumber.replaceAll("-", "");
	}

	/**
	 * 사업자번호 포맷팅 (하이픈 추가)
	 */
	public String format(String businessNumber) {
		if (businessNumber == null || businessNumber.length() != 10) {
			return businessNumber;
		}

		// XXX-XX-XXXXX 형태로 포맷팅
		return businessNumber.substring(0, 3) + "-" +
			businessNumber.substring(3, 5) + "-" +
			businessNumber.substring(5);
	}

	/**
	 * 사업자번호 마스킹 (보안용)
	 */
	public String mask(String businessNumber) {
		if (businessNumber == null || businessNumber.length() < 5) {
			return "***-**-*****";
		}

		String normalized = normalize(businessNumber);
		if (normalized.length() != 10) {
			return "***-**-*****";
		}

		// 앞 3자리만 표시, 나머지 마스킹
		return normalized.substring(0, 3) + "-**-*****";
	}
}
