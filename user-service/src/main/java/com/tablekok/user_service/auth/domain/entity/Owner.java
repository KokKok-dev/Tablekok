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
	@Index(name = "idx_owner_business_number", columnList = "business_number"),
	@Index(name = "idx_owner_user_id", columnList = "user_id")
})
@Getter
@Builder(access = AccessLevel.PRIVATE)
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
	 * ✅ 피드백 반영: 검증은 DomainService에서 수행하므로 여기서는 제거
	 * 양방향 연관관계 자동 설정
	 *
	 * @param user Owner 역할의 User 엔티티 (이미 검증된 상태)
	 * @param businessNumber 사업자번호 (이미 검증된 상태, 정규화되지 않은 원본)
	 * @return 생성된 Owner 엔티티
	 */
	public static Owner create(User user, String businessNumber) {
		// ✅ 피드백 반영: 검증 로직 제거 - DomainService에서 사전 검증 완료
		// validateUser(user); - 제거
		// validateBusinessNumber(businessNumber); - 제거

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

	// ========== ❌ 제거된 검증 메서드들 (피드백 반영) ==========
	// validateUser() - AuthDomainService로 이동
	// validateBusinessNumber() - BusinessNumberValidator로 이동

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
	 * ✅ 피드백 반영: 검증은 DomainService에서 수행
	 */
	public void assignUser(User user) {
		// ✅ 검증은 DomainService에서 수행하므로 여기서는 제거
		this.user = user;
		user.assignBusinessNumber(this.businessNumber);
	}

	/**
	 * 사업자번호 업데이트
	 * ✅ 피드백 반영: 검증은 DomainService에서 수행
	 */
	public void updateBusinessNumber(String newBusinessNumber) {
		// ✅ 검증은 DomainService에서 수행하므로 여기서는 제거
		String normalized = normalizeBusinessNumber(newBusinessNumber);
		this.businessNumber = normalized;
		this.user.assignBusinessNumber(normalized);
	}
}
