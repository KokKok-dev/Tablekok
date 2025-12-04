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
import java.util.regex.Pattern;

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

	// ========== Domain 정적 팩토리 메서드 (기존) ==========

	/**
	 * Customer 생성 정적 팩토리 메서드
	 */
	public static User createCustomer(String email, String name, String password, String phoneNumber) {
		// Domain 검증 적용
		validateEmail(email);
		validateName(name);
		validatePassword(password);
		validatePhoneNumber(phoneNumber);

		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(password)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.CUSTOMER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Owner 생성 정적 팩토리 메서드
	 */
	public static User createOwner(String email, String name, String password, String phoneNumber) {
		// Domain 검증 적용
		validateEmail(email);
		validateName(name);
		validatePassword(password);
		validatePhoneNumber(phoneNumber);

		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(password)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.OWNER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	/**
	 * Master 생성 정적 팩토리 메서드
	 */
	public static User createMaster(String email, String name, String password, String phoneNumber) {
		// Domain 검증 적용
		validateEmail(email);
		validateName(name);
		validatePassword(password);
		validatePhoneNumber(phoneNumber);

		return User.builder()
			.email(normalizeEmail(email))
			.name(name.trim())
			.password(password)
			.phoneNumber(normalizePhoneNumber(phoneNumber))
			.role(UserRole.MASTER)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	// ========== Domain 정규화 메서드들 (Application Service에서 이동) ==========

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

	// ========== Domain 검증 메서드들 (Application Service에서 이동) ==========

	/**
	 * 이메일 도메인 검증
	 *
	 * @param email 검증할 이메일
	 * @throws IllegalArgumentException 유효하지 않은 이메일인 경우
	 */
	public static void validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수 입력 값입니다.");
		}

		String normalized = normalizeEmail(email);

		// 기본 이메일 형식 검증
		if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", normalized)) {
			throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
		}

		// 이메일 길이 검증
		if (normalized.length() > 100) {
			throw new IllegalArgumentException("이메일은 100자를 초과할 수 없습니다.");
		}
	}

	/**
	 * 이름 도메인 검증
	 *
	 * @param name 검증할 이름
	 * @throws IllegalArgumentException 유효하지 않은 이름인 경우
	 */
	public static void validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("이름은 필수 입력 값입니다.");
		}

		String trimmed = name.trim();

		if (trimmed.length() < 2 || trimmed.length() > 50) {
			throw new IllegalArgumentException("이름은 2자 이상 50자 이하로 입력해주세요.");
		}

		if (!Pattern.matches("^[가-힣a-zA-Z\\s]+$", trimmed)) {
			throw new IllegalArgumentException("이름은 한글, 영문, 공백만 허용됩니다.");
		}
	}

	/**
	 * 비밀번호 도메인 검증
	 *
	 * @param password 검증할 비밀번호
	 * @throws IllegalArgumentException 유효하지 않은 비밀번호인 경우
	 */
	public static void validatePassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수 입력 값입니다.");
		}

		if (password.length() < 8 || password.length() > 20) {
			throw new IllegalArgumentException("비밀번호는 8자 이상 20자 이하로 입력해주세요.");
		}

		if (!Pattern.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", password)) {
			throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.");
		}
	}

	/**
	 * 휴대폰번호 도메인 검증
	 *
	 * @param phoneNumber 검증할 휴대폰번호
	 * @throws IllegalArgumentException 유효하지 않은 휴대폰번호인 경우
	 */
	public static void validatePhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("휴대폰번호는 필수 입력 값입니다.");
		}

		String normalized = normalizePhoneNumber(phoneNumber);

		if (!Pattern.matches("^01[0-9]{8,9}$", normalized)) {
			throw new IllegalArgumentException("올바른 휴대폰번호 형식이 아닙니다. (01X + 8~9자리 숫자)");
		}
	}

	// ========== Domain 비즈니스 메서드들 (기존) ==========

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
	 * 비밀번호 업데이트
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
