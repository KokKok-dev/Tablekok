package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_user", indexes = {
	@Index(name = "idx_user_email", columnList = "email"),
	@Index(name = "idx_user_phone_number", columnList = "phone_number"),
	@Index(name = "idx_user_role", columnList = "role")
})
@Getter
@Builder(access = AccessLevel.PRIVATE)  // gyoseok17 피드백: PRIVATE
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_id", columnDefinition = "UUID")
	private UUID userId;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "phone_number", nullable = false, unique = true, length = 20)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private UserRole role;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Column(name = "login_count", nullable = false)
	private Integer loginCount;

	@Column(name = "business_number", length = 12)
	private String businessNumber;  // Owner인 경우만 사용

	// ========== Domain 정적 팩토리 메서드 (gashine20 피드백 반영) ==========

	/**
	 * Customer 생성 정적 팩토리 메서드
	 * ✅ gashine20 피드백 반영: 검증 로직 제거, UserValidator에서 처리
	 */
	public static User createCustomer(String email, String name, String encodedPassword, String phoneNumber) {
		// ✅ 검증은 UserValidator에서 수행하므로 여기서는 제거
		// 정규화만 수행
		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(encodedPassword)  // 이미 암호화된 비밀번호
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.CUSTOMER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Owner 생성 정적 팩토리 메서드
	 * ✅ gashine20 피드백 반영: 검증 로직 제거, UserValidator에서 처리
	 */
	public static User createOwner(String email, String name, String encodedPassword, String phoneNumber) {
		// ✅ 검증은 UserValidator에서 수행하므로 여기서는 제거
		// 정규화만 수행
		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(encodedPassword)  // 이미 암호화된 비밀번호
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.OWNER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Master 생성 정적 팩토리 메서드
	 * ✅ gashine20 피드백 반영: 검증 로직 제거, UserValidator에서 처리
	 */
	public static User createMaster(String email, String name, String encodedPassword, String phoneNumber) {
		// ✅ 검증은 UserValidator에서 수행하므로 여기서는 제거
		// 정규화만 수행
		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(encodedPassword)  // 이미 암호화된 비밀번호
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.MASTER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	// ========== Domain 정규화 메서드들 (유지) ==========

	/**
	 * 이메일 정규화 (Domain 규칙)
	 * - 소문자 변환
	 * - 앞뒤 공백 제거
	 *
	 * @param email 원본 이메일
	 * @return 정규화된 이메일
	 */
	public static String normalizeEmail(String email) {
		if (email == null) {
			return null;
		}
		return email.toLowerCase().trim();
	}

	/**
	 * 휴대폰번호 정규화 (Domain 규칙)
	 * - 하이픈 제거
	 * - 공백 제거
	 *
	 * @param phoneNumber 원본 휴대폰번호
	 * @return 정규화된 휴대폰번호
	 */
	public static String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			return null;
		}
		return phoneNumber.replaceAll("-", "").replaceAll("\\s", "");
	}

	// ========== ❌ 제거된 검증 메서드들 (gashine20 피드백 반영) ==========
	// public static void validateEmail() - UserValidator로 이동
	// public static void validateName() - UserValidator로 이동
	// public static void validatePassword() - UserValidator로 이동
	// public static void validatePhoneNumber() - UserValidator로 이동

	// ========== Domain 비즈니스 메서드들 (유지) ==========

	/**
	 * 계정 활성 여부 확인
	 */
	public boolean isAccountActive() {
		return Boolean.TRUE.equals(this.isActive) && this.getDeletedAt() == null;
	}

	/**
	 * 로그인 정보 업데이트
	 */
	public void updateLoginInfo() {
		this.lastLoginAt = LocalDateTime.now();
		this.loginCount = (this.loginCount == null) ? 1 : this.loginCount + 1;
	}

	/**
	 * 비밀번호 업데이트 (이미 암호화된 비밀번호)
	 */
	public void updatePassword(String newEncodedPassword) {
		if (newEncodedPassword == null || newEncodedPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다.");
		}
		this.password = newEncodedPassword;
	}

	/**
	 * 계정 비활성화
	 */
	public void deactivate() {
		this.isActive = false;
	}

	/**
	 * 계정 활성화
	 */
	public void activate() {
		this.isActive = true;
	}

	// ========== Domain 역할 확인 메서드들 ==========

	public boolean isCustomer() {
		return UserRole.CUSTOMER == this.role;
	}

	public boolean isOwner() {
		return UserRole.OWNER == this.role;
	}

	public boolean isMaster() {
		return UserRole.MASTER == this.role;
	}

	// ========== Owner 전용 메서드 ==========

	/**
	 * Owner 사업자번호 설정 (연관관계 편의 메서드)
	 */
	public void assignBusinessNumber(String businessNumber) {
		if (!isOwner()) {
			throw new IllegalStateException("Owner 역할만 사업자번호를 가질 수 있습니다.");
		}
		this.businessNumber = businessNumber;
	}

	/**
	 * 사업자번호 조회 (Owner인 경우만)
	 */
	public String getBusinessNumber() {
		return isOwner() ? this.businessNumber : null;
	}
}
