package com.tablekok.search_service.application.dto.result;

import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.document.StoreStatus;

import lombok.Builder;

@Builder
public record SearchStoreResult(
	String storeId,
	String name,
	Double averageRating,
	Long reviewCount,
	String imageUrl,
	StoreStatus status
) {
	public static SearchStoreResult fromResult(StoreDocument store) {
		return SearchStoreResult.builder()
			.storeId(store.getStoreId())
			.name(store.getName())
			.averageRating(store.getAverageRating())
			.reviewCount(store.getReviewCount())
			.imageUrl(store.getImageUrl())
			.status(store.getStatus())
			.build();
	}
}
