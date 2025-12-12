package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_owner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Owner extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "owner_id", columnDefinition = "UUID")
	private UUID ownerId;

	@Column(name = "user_id", nullable = false, unique = true, columnDefinition = "UUID")
	private UUID userId;

	@Column(name = "business_number", nullable = false, unique = true, length = 12)
	private String businessNumber;

	public static Owner create(UUID userId, String businessNumber) {
		return Owner.builder()
			.userId(userId)
			.businessNumber(businessNumber)
			.build();
	}
}
