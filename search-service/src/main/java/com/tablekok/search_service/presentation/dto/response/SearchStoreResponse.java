package com.tablekok.search_service.presentation.dto.response;

import com.tablekok.search_service.application.dto.result.SearchStoreResult;
import com.tablekok.search_service.domain.document.StoreStatus;

import lombok.Builder;

@Builder
public record SearchStoreResponse(
	String storeId,
	String name,
	Double averageRating,
	Long reviewCount,
	String imageUrl,
	StoreStatus status
) {
	public static SearchStoreResponse from(SearchStoreResult result) {
		return SearchStoreResponse.builder()
			.storeId(result.storeId())
			.name(result.name())
			.averageRating(result.averageRating())
			.reviewCount(result.reviewCount())
			.imageUrl(result.imageUrl())
			.status(result.status())
			.build();
	}
}
