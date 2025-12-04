// auth/application/dto/OwnerSignupParam.java
package com.tablekok.user_service.auth.application.dto;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

/**
 * 사장님 회원가입 Application Layer DTO (Param)
 * 코드 컨벤션: Create<Entity>Param 패턴 적용
 *
 * 순수 데이터 전송 객체 - 비즈니스 로직은 Service에서 처리
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
}
