package com.tablekok.review_service.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLRestriction;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is NULL")
@Table(
	name = "p_review",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_review_reservation", columnNames = {"reservation_id"})
	},
	indexes = {
		@Index(name = "idx_store_created_at", columnList = "store_id, created_at DESC, review_id"), // 가게별 최신순 인덱스
		@Index(name = "idx_store_rating_id", columnList = "store_id, rating DESC, review_id"), // 가게별 별점순 인덱스
		@Index(name = "idx_store_created_at", columnList = "user_id, created_at DESC, review_id") // 사용자별 최신순 인덱스
	}
)
@Check(constraints = "rating >= 0 AND rating <= 5")
public class Review extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "review_id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "reservation_id", nullable = false)
	private UUID reservationId;

	@Column(name = "rating", nullable = false)
	private Double rating;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	// @Column(name = "image_url", nullable = false)
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
