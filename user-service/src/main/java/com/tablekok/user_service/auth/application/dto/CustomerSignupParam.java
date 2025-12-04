// auth/application/dto/CustomerSignupParam.java
package com.tablekok.user_service.auth.application.dto;

import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

/**
 * 고객 회원가입 Application Layer DTO (Param)
 * 코드 컨벤션: Create<Entity>Param 패턴 적용
 *
 * Presentation Layer의 Request DTO에서 변환되어 
 * Application Service에서 사용되는 순수 데이터 객체
 */
@Builder
public record CustomerSignupParam(
	String email,
	String username,
	String password,
	String phone
) {

	/**
	 * CustomerSignupParam을 User Entity로 변환
	 * User.createCustomer() 정적 팩토리 메서드 활용
	 *
	 * @param encodedPassword BCrypt로 인코딩된 비밀번호
	 * @return 생성된 Customer User 엔티티
	 */
	public User toEntity(String encodedPassword) {
		return User.createCustomer(
			this.email,
			this.username,
			encodedPassword,
			this.phone
		);
	}

	/**
	 * 이메일 정규화된 값 반환
	 *
	 * @return 소문자 변환 + 공백 제거된 이메일
	 */
	public String getNormalizedEmail() {
		return email != null ? email.toLowerCase().trim() : null;
	}

	/**
	 * 휴대폰번호 정규화된 값 반환
	 *
	 * @return 하이픈이 제거된 휴대폰번호
	 */
	public String getNormalizedPhone() {
		return phone != null ? phone.replaceAll("-", "") : null;
	}

	/**
	 * 유효성 검증을 위한 기본 체크
	 * (실제 검증은 Validation 어노테이션에서 수행)
	 *
	 * @return 필수 필드가 모두 존재하면 true
	 */
	public boolean hasRequiredFields() {
		return email != null && !email.trim().isEmpty() &&
			username != null && !username.trim().isEmpty() &&
			password != null && !password.trim().isEmpty() &&
			phone != null && !phone.trim().isEmpty();
	}

	/**
	 * 디버깅용 문자열 (비밀번호 마스킹)
	 */
	@Override
	public String toString() {
		return "CustomerSignupParam{" +
			"email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", username='" + username + '\'' +
			", password='***'" +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			'}';
	}
}
