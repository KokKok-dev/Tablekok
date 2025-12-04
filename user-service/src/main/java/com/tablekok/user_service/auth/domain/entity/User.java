// auth/domain/entity/User.java
package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;
import com.tablekok.user_service.auth.domain.enums.UserRole;
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
@Builder(access = AccessLevel.PRIVATE)  // ← PRIVATE으로 변경 (gyoseok17 피드백)
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

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Owner owner;

	// ========== 정적 팩토리 메서드들 ==========

	/**
	 * Customer 사용자 생성
	 */
	public static User createCustomer(String email, String name, String encodedPassword, String phoneNumber) {
		return User.builder()
			.email(normalizeEmail(email))
			.name(name)
			.password(encodedPassword)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.CUSTOMER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Owner 사용자 생성
	 */
	public static User createOwner(String email, String name, String encodedPassword, String phoneNumber) {
		return User.builder()
			.email(normalizeEmail(email))
			.name(name)
			.password(encodedPassword)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.OWNER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Master 사용자 생성
	 */
	public static User createMaster(String email, String name, String encodedPassword, String phoneNumber) {
		return User.builder()
			.email(normalizeEmail(email))
			.name(name)
			.password(encodedPassword)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.MASTER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	// ========== 비즈니스 메서드들 ==========

	/**
	 * 비밀번호 변경
	 */
	public void updatePassword(String encodedNewPassword) {
		this.password = encodedNewPassword;
	}

	/**
	 * 프로필 업데이트 (Customer용)
	 */
	public void updateCustomerProfile(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = normalizePhoneNumber(phoneNumber);
	}

	/**
	 * 프로필 업데이트 (Owner용 - 사업자번호는 Owner 엔티티에서 처리)
	 */
	public void updateOwnerProfile(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = normalizePhoneNumber(phoneNumber);
	}

	/**
	 * 로그인 정보 업데이트
	 */
	public void updateLoginInfo() {
		this.lastLoginAt = LocalDateTime.now();
		this.loginCount = this.loginCount + 1;
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

	/**
	 * 계정 활성 상태 확인
	 */
	public boolean isAccountActive() {
		return this.isActive && this.getDeletedAt() == null;
	}

	// ========== 권한 체크 메서드들 ==========

	/**
	 * 고객인지 확인
	 */
	public boolean isCustomer() {
		return this.role == UserRole.CUSTOMER;
	}

	/**
	 * 사장님인지 확인
	 */
	public boolean isOwner() {
		return this.role == UserRole.OWNER;
	}

	/**
	 * 마스터 관리자인지 확인
	 */
	public boolean isMaster() {
		return this.role == UserRole.MASTER;
	}

	// ========== Owner 연관관계 메서드들 ==========

	/**
	 * Owner 엔티티 연결
	 */
	public void assignOwner(Owner owner) {
		this.owner = owner;
		if (owner != null && owner.getUser() != this) {
			owner.assignUser(this);
		}
	}

	/**
	 * 사업자번호 조회 (Owner인 경우만)
	 */
	public String getBusinessNumber() {
		return this.owner != null ? this.owner.getBusinessNumber() : null;
	}

	/**
	 * Owner 정보 존재 여부 확인
	 */
	public boolean hasOwnerInfo() {
		return this.owner != null;
	}

	// ========== 내부 유틸리티 메서드들 ==========

	/**
	 * 이메일 정규화 (소문자 변환 + 공백 제거)
	 */
	private static String normalizeEmail(String email) {
		return email != null ? email.toLowerCase().trim() : null;
	}

	/**
	 * 휴대폰번호 정규화 (하이픈 제거)
	 */
	private static String normalizePhoneNumber(String phoneNumber) {
		return phoneNumber != null ? phoneNumber.replaceAll("-", "") : null;
	}

	// ========== 도메인 검증 메서드들 ==========

	/**
	 * 이메일 형식 간단 검증 (더 정교한 검증은 Validation 어노테이션에서)
	 */
	public boolean isValidEmail() {
		return this.email != null && this.email.contains("@") && this.email.contains(".");
	}

	/**
	 * 휴대폰번호 형식 간단 검증
	 */
	public boolean isValidPhoneNumber() {
		return this.phoneNumber != null &&
			this.phoneNumber.matches("^01[0-9]{8,9}$");
	}

	/**
	 * 비밀번호 존재 여부 확인 (실제 검증은 PasswordEncoder에서)
	 */
	public boolean hasPassword() {
		return this.password != null && !this.password.trim().isEmpty();
	}
}
