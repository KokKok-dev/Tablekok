package com.tablekok.user_service.auth.application.dto.command;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

/**
 * 사장님 회원가입 Application Layer DTO (Command)
 * 코드 컨벤션: Create<Entity>Command 패턴 적용
 *
 * 순수 데이터 전송 객체 - 비즈니스 로직은 Service에서 처리
 * Customer 정보 + 사업자번호 포함
 */
@Builder
public record OwnerSignupCommand(
	String email,
	String username,
	String password,
	String phone,
	String businessNumber
) {

	/**
	 * OwnerSignupCommand를 Owner User Entity로 변환
	 * User Entity 생성만 담당 (Owner Entity는 Service에서 별도 생성)
	 *
	 * @param encodedPassword BCrypt로 인코딩된 비밀번호
	 * @return 생성된 Owner User 엔티티 (OWNER 역할)
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
	 * OwnerSignupCommand를 Owner Entity로 변환
	 * User Entity와 연관관계 설정
	 *
	 * @param ownerUser 이미 생성된 Owner User 엔티티
	 * @return 생성된 Owner 엔티티
	 */
	public Owner toOwnerEntity(User ownerUser) {
		return Owner.create(ownerUser, this.businessNumber);
	}
}
