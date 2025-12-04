// auth/domain/entity/Owner.java
package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_owner", indexes = {
	@Index(name = "idx_owner_business_number", columnList = "business_number")
})
@Getter
@Builder(access = AccessLevel.PRIVATE)  // ← PRIVATE으로 변경 (gyoseok17 피드백)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends BaseEntity {

	@Id
	@Column(name = "user_id", columnDefinition = "UUID")
	private UUID userId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "business_number", nullable = false, unique = true, length = 12)
	private String businessNumber;

	// ========== 정적 팩토리 메서드 ==========

	/**
	 * Owner 엔티티 생성
	 */
	public static Owner create(User user, String businessNumber) {
		Owner owner = Owner.builder()
			.user(user)
			.businessNumber(normalizeBusinessNumber(businessNumber))
			.build();

		// 양방향 연관관계 설정
		user.assignOwner(owner);

		return owner;
	}

	// ========== 비즈니스 메서드들 ==========

	/**
	 * 사업자번호 업데이트
	 */
	public void updateBusinessNumber(String businessNumber) {
		this.businessNumber = normalizeBusinessNumber(businessNumber);
	}

	/**
	 * User 연결 (양방향 연관관계 설정)
	 */
	public void assignUser(User user) {
		this.user = user;
		this.userId = user.getUserId();
	}

	/**
	 * 사업자번호 마스킹 (보안용)
	 * 예: 123-45-67890 → 123-**-****0
	 */
	public String getMaskedBusinessNumber() {
		if (businessNumber == null || businessNumber.length() < 10) {
			return "***-**-****";
		}

		String formatted = formatBusinessNumber(businessNumber);
		return formatted.substring(0, 3) + "-**-****" + formatted.substring(formatted.length() - 1);
	}

	/**
	 * 포맷된 사업자번호 반환 (XXX-XX-XXXXX 형태)
	 */
	public String getFormattedBusinessNumber() {
		return formatBusinessNumber(this.businessNumber);
	}

	// ========== 도메인 검증 메서드들 ==========

	/**
	 * 사업자번호 형식 검증 (기본 길이 체크)
	 */
	public boolean isValidBusinessNumberFormat() {
		return this.businessNumber != null &&
			this.businessNumber.length() == 10 &&
			this.businessNumber.matches("^[0-9]{10}$");
	}

	/**
	 * User와의 연관관계 확인
	 */
	public boolean isLinkedToUser() {
		return this.user != null && this.userId != null;
	}

	// ========== 내부 유틸리티 메서드들 ==========

	/**
	 * 사업자번호 정규화 (하이픈 제거)
	 */
	private static String normalizeBusinessNumber(String businessNumber) {
		return businessNumber != null ? businessNumber.replaceAll("-", "") : null;
	}

	/**
	 * 사업자번호 포맷팅 (XXX-XX-XXXXX 형태로 변환)
	 */
	private String formatBusinessNumber(String businessNumber) {
		if (businessNumber == null || businessNumber.length() != 10) {
			return businessNumber;
		}

		return businessNumber.substring(0, 3) + "-" +
			businessNumber.substring(3, 5) + "-" +
			businessNumber.substring(5);
	}
}
