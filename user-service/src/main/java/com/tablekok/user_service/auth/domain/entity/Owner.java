package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "p_owner", indexes = {
	@Index(name = "idx_owner_business_number", columnList = "business_number"),
	@Index(name = "idx_owner_user_id", columnList = "user_id")
})
@Getter
@Builder(access = AccessLevel.PRIVATE)  // gyoseok17 피드백: PRIVATE
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "owner_id", columnDefinition = "UUID")
	private UUID ownerId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "business_number", nullable = false, unique = true, length = 12)
	private String businessNumber;

	// ========== Domain 정적 팩토리 메서드 ==========

	/**
	 * Owner 생성 정적 팩토리 메서드
	 * 양방향 연관관계 자동 설정
	 *
	 * @param user Owner 역할의 User 엔티티
	 * @param businessNumber 사업자번호 (정규화되지 않은 원본)
	 * @return 생성된 Owner 엔티티
	 */
	public static Owner create(User user, String businessNumber) {
		// Domain 검증
		validateUser(user);
		validateBusinessNumber(businessNumber);

		String normalizedBusinessNumber = normalizeBusinessNumber(businessNumber);

		Owner owner = Owner.builder()
			.user(user)
			.businessNumber(normalizedBusinessNumber)
			.build();

		// 양방향 연관관계 설정
		user.assignBusinessNumber(normalizedBusinessNumber);

		return owner;
	}

	// ========== Domain 정규화 메서드 ==========

	/**
	 * 사업자번호 정규화 (Domain 규칙)
	 * - 하이픈 제거
	 * - 공백 제거
	 *
	 * @param businessNumber 원본 사업자번호
	 * @return 정규화된 사업자번호
	 */
	public static String normalizeBusinessNumber(String businessNumber) {
		if (businessNumber == null) {
			return null;
		}
		return businessNumber.replaceAll("-", "").replaceAll("\\s", "");
	}

	// ========== Domain 검증 메서드 ==========

	/**
	 * User 엔티티 검증
	 *
	 * @param user 검증할 User 엔티티
	 * @throws IllegalArgumentException User가 유효하지 않은 경우
	 */
	public static void validateUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User는 필수입니다.");
		}

		if (!user.isOwner()) {
			throw new IllegalArgumentException("Owner 역할의 User만 Owner 엔티티를 생성할 수 있습니다.");
		}
	}

	/**
	 * 사업자번호 도메인 검증 (기본 형식만)
	 * 상세 체크섬 검증은 BusinessNumberValidator에서 수행
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws IllegalArgumentException 유효하지 않은 사업자번호인 경우
	 */
	public static void validateBusinessNumber(String businessNumber) {
		if (businessNumber == null || businessNumber.trim().isEmpty()) {
			throw new IllegalArgumentException("사업자번호는 필수 입력 값입니다.");
		}

		String normalized = normalizeBusinessNumber(businessNumber);

		// 기본 형식 검증 (10자리 숫자)
		if (!Pattern.matches("^\\d{10}$", normalized)) {
			throw new IllegalArgumentException("사업자번호는 10자리 숫자여야 합니다.");
		}
	}

	// ========== Domain 비즈니스 메서드들 ==========

	/**
	 * 마스킹된 사업자번호 반환 (보안용)
	 * XXX-**-****0 형태
	 */
	public String getMaskedBusinessNumber() {
		if (businessNumber == null || businessNumber.length() != 10) {
			return "***-**-*****";
		}

		return businessNumber.substring(0, 3) + "-**-****" + businessNumber.charAt(9);
	}

	/**
	 * 포맷된 사업자번호 반환 (화면 표시용)
	 * XXX-XX-XXXXX 형태
	 */
	public String getFormattedBusinessNumber() {
		if (businessNumber == null || businessNumber.length() != 10) {
			return businessNumber;
		}

		return businessNumber.substring(0, 3) + "-" +
			businessNumber.substring(3, 5) + "-" +
			businessNumber.substring(5);
	}

	/**
	 * User 연관관계 업데이트
	 */
	public void assignUser(User user) {
		validateUser(user);
		this.user = user;
		user.assignBusinessNumber(this.businessNumber);
	}

	/**
	 * 사업자번호 업데이트
	 */
	public void updateBusinessNumber(String newBusinessNumber) {
		validateBusinessNumber(newBusinessNumber);
		String normalized = normalizeBusinessNumber(newBusinessNumber);
		this.businessNumber = normalized;
		this.user.assignBusinessNumber(normalized);
	}
}
