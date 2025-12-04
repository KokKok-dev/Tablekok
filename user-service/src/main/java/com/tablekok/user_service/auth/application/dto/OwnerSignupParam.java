// auth/application/dto/OwnerSignupParam.java
package com.tablekok.user_service.auth.application.dto;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

/**
 * 사장님 회원가입 Application Layer DTO (Param)
 * 코드 컨벤션: Create<Entity>Param 패턴 적용
 *
 * User + Owner 두 엔티티를 생성해야 하는 복합 비즈니스 로직 처리
 */
@Builder
public record OwnerSignupParam(
	String email,
	String username,
	String password,
	String phone,
	String businessNumber
) {

	/**
	 * OwnerSignupParam을 User Entity로 변환
	 * Owner 역할의 User 생성
	 *
	 * @param encodedPassword BCrypt로 인코딩된 비밀번호
	 * @return 생성된 Owner User 엔티티
	 */
	public User toUserEntity(String encodedPassword) {
		return User.createOwner(
			this.email,
			this.username,
			encodedPassword,
			this.phone
		);
	}

	/**
	 * User 엔티티를 기반으로 Owner Entity 생성
	 *
	 * @param user 이미 생성된 User 엔티티
	 * @return 생성된 Owner 엔티티
	 */
	public Owner toOwnerEntity(User user) {
		return Owner.create(user, this.businessNumber);
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
	 * 사업자번호 정규화된 값 반환
	 *
	 * @return 하이픈이 제거된 사업자번호
	 */
	public String getNormalizedBusinessNumber() {
		return businessNumber != null ? businessNumber.replaceAll("-", "") : null;
	}

	/**
	 * 유효성 검증을 위한 기본 체크
	 * Owner는 추가로 사업자번호 필수
	 *
	 * @return 필수 필드가 모두 존재하면 true
	 */
	public boolean hasRequiredFields() {
		return email != null && !email.trim().isEmpty() &&
			username != null && !username.trim().isEmpty() &&
			password != null && !password.trim().isEmpty() &&
			phone != null && !phone.trim().isEmpty() &&
			businessNumber != null && !businessNumber.trim().isEmpty();
	}

	/**
	 * 사업자번호 포맷팅된 값 반환
	 * XXX-XX-XXXXX 형태
	 *
	 * @return 포맷팅된 사업자번호
	 */
	public String getFormattedBusinessNumber() {
		String normalized = getNormalizedBusinessNumber();
		if (normalized == null || normalized.length() != 10) {
			return businessNumber;
		}

		return normalized.substring(0, 3) + "-" +
			normalized.substring(3, 5) + "-" +
			normalized.substring(5);
	}

	/**
	 * 디버깅용 문자열 (민감정보 마스킹)
	 */
	@Override
	public String toString() {
		return "OwnerSignupParam{" +
			"email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", username='" + username + '\'' +
			", password='***'" +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			", businessNumber='" + (businessNumber != null ? businessNumber.substring(0, Math.min(3, businessNumber.length())) + "***" : "null") + '\'' +
			'}';
	}
}
