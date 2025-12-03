package com.tablekok.user_service.domain.entity;

import com.tablekok.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_owner", indexes = {
	@Index(name = "idx_owner_business_number", columnList = "businessNumber")
})
public class Owner extends BaseEntity {

	@Id
	@Column(name = "user_id")
	private UUID userId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, unique = true, length = 20, name = "business_number")
	private String businessNumber;

	@Builder
	public Owner(User user, String businessNumber) {
		this.user = user;
		this.userId = user.getUserId();
		this.businessNumber = businessNumber;
	}

	// 비즈니스 메서드
	public void updateBusinessNumber(String newBusinessNumber) {
		this.businessNumber = newBusinessNumber;
	}

	public boolean isValidBusinessNumber() {
		// XXX-XX-XXXXX 형태 검증 (정규표현식)
		return businessNumber != null &&
			businessNumber.matches("\\d{3}-\\d{2}-\\d{5}");
	}

	public String getFormattedBusinessNumber() {
		return businessNumber;
	}

	public String getUnformattedBusinessNumber() {
		return businessNumber.replaceAll("-", "");
	}
}
