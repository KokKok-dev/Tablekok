package com.tablekok.user_service.auth.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Entity DTO (Entity 직접 노출 방지용)
 * 피드백: Service → Controller는 DTO로만 반환
 *
 * Controller에서 User Entity 직접 참조를 방지하는 목적
 */
@Builder
public record UserDto(
	UUID userId,
	String email,
	String username,
	String phone,
	UserRole role,
	String businessNumber,  // Owner인 경우만
	Boolean isActive,
	LocalDateTime lastLoginAt,
	Integer loginCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	/**
	 * User Entity → UserDto 변환
	 *
	 * @param user User Entity
	 * @return UserDto
	 */
	public static UserDto from(User user) {
		return UserDto.builder()
			.userId(user.getUserId())
			.email(user.getEmail())
			.username(user.getName())
			.phone(user.getPhoneNumber())
			.role(user.getRole())
			.businessNumber(user.getBusinessNumber())  // Owner인 경우만 존재
			.isActive(user.getIsActive())
			.lastLoginAt(user.getLastLoginAt())
			.loginCount(user.getLoginCount())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isCustomer() {
		return UserRole.CUSTOMER == role;
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isOwner() {
		return UserRole.OWNER == role;
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isMaster() {
		return UserRole.MASTER == role;
	}

	/**
	 * 사업자번호 존재 여부 확인
	 */
	public boolean hasBusinessNumber() {
		return businessNumber != null && !businessNumber.trim().isEmpty();
	}

	/**
	 * 계정 활성 상태 확인
	 */
	public boolean isAccountActive() {
		return Boolean.TRUE.equals(isActive);
	}
}
