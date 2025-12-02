package com.tablekok.review_service.domain.entity;

import java.util.UUID;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_review")
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "review_id", columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private UUID storeId;

	@Column(nullable = false)
	private Double rating;

	@Column(nullable = false)
	private String content;

	// @Column(nullable = false)
	// private String imageUrl;

	@Builder(access = AccessLevel.PRIVATE)
	private Review(
		UUID id,
		UUID userId,
		UUID storeId,
		Double rating,
		String content
	) {
		this.id = id;
		this.userId = userId;
		this.storeId = storeId;
		this.rating = rating;
		this.content = content;
	}

	public static Review create(
		UUID id,
		UUID userId,
		UUID storeId,
		Double rating,
		String content
	) {
		return new Review(
			id,
			userId,
			storeId,
			rating,
			content
		);
	}
}
