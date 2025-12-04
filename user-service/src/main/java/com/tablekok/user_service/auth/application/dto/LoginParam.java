// auth/application/dto/LoginParam.java
package com.tablekok.user_service.auth.application.dto;

import lombok.Builder;

/**
 * 로그인 Application Layer DTO (Param)
 * 모든 역할군(CUSTOMER, OWNER, MASTER) 공통 사용
 *
 * 단순한 이메일/비밀번호 조합으로 인증 수행
 */
@Builder
public record LoginParam(
	String email,
	String password
) {

	/**
	 * 이메일 정규화된 값 반환
	 *
	 * @return 소문자 변환 + 공백 제거된 이메일
	 */
	public String getNormalizedEmail() {
		return email != null ? email.toLowerCase().trim() : null;
	}

	/**
	 * 유효성 검증을 위한 기본 체크
	 *
	 * @return 이메일과 비밀번호가 모두 존재하면 true
	 */
	public boolean hasRequiredFields() {
		return email != null && !email.trim().isEmpty() &&
			password != null && !password.trim().isEmpty();
	}

	/**
	 * 이메일 도메인 추출
	 *
	 * @return 이메일의 도메인 부분 (@example.com)
	 */
	public String getEmailDomain() {
		if (email == null || !email.contains("@")) {
			return null;
		}

		int atIndex = email.lastIndexOf("@");
		return email.substring(atIndex + 1).toLowerCase();
	}

	/**
	 * 이메일 로컬 부분 추출
	 *
	 * @return 이메일의 사용자명 부분 (user@example.com → user)
	 */
	public String getEmailLocalPart() {
		if (email == null || !email.contains("@")) {
			return email;
		}

		int atIndex = email.lastIndexOf("@");
		return email.substring(0, atIndex).toLowerCase();
	}

	/**
	 * 디버깅용 문자열 (비밀번호 마스킹)
	 */
	@Override
	public String toString() {
		return "LoginParam{" +
			"email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", password='***'" +
			'}';
	}
}
