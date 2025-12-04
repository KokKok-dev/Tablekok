package com.tablekok.review_service.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

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
@SQLRestriction("deleted_at is NULL")
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
	private UUID reservationId;

	@Column(nullable = false)
	private Double rating;

	@Column(nullable = false)
	private String content;

	// @Column(nullable = false)
	// private String imageUrl;

	@Builder(access = AccessLevel.PRIVATE)
	private Review(
		UUID userId,
		UUID storeId,
		UUID reservationId,
		Double rating,
		String content
	) {
		this.userId = userId;
		this.storeId = storeId;
		this.reservationId = reservationId;
		this.rating = rating;
		this.content = content;
	}

	public static Review create(
		UUID userId,
		UUID storeId,
		UUID reservationId,
		Double rating,
		String content
	) {
		return Review.builder()
			.userId(userId)
			.storeId(storeId)
			.reservationId(reservationId)
			.rating(rating)
			.content(content)
			.build();
	}

	public void updateReview(Double rating, String content) {
		this.rating = rating != null ? rating : this.rating;
		this.content = content != null ? content : this.content;
	}

	public void softDelete(UUID deleterId) {
		super.delete(deleterId);
	}
}
