package com.tablekok.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.review_service.domain.entity.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {
	boolean existsByReservationId(UUID reservationId);

	long countByStoreId(UUID storeId);

	long countByUserId(UUID userId);
}
