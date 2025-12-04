// auth/application/dto/CustomerSignupParam.java
package com.tablekok.user_service.auth.application.dto;

import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

/**
 * 고객 회원가입 Application Layer DTO (Param)
 * 코드 컨벤션: Create<Entity>Param 패턴 적용
 *
 * 순수 데이터 전송 객체 - 비즈니스 로직은 Service에서 처리
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
	 * 단순 데이터 변환만 담당
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
}
