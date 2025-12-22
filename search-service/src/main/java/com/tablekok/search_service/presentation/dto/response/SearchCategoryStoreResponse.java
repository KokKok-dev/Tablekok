package com.tablekok.search_service.presentation.dto.response;

import com.tablekok.search_service.application.dto.result.SearchCategoryStoreResult;
import com.tablekok.search_service.domain.document.StoreStatus;

import lombok.Builder;

@Builder
public record SearchCategoryStoreResponse(
	String storeId,
	String name,
	Double averageRating,
	Long reviewCount,
	String imageUrl,
	StoreStatus status
) {
	public static SearchCategoryStoreResponse from(SearchCategoryStoreResult result) {
		return SearchCategoryStoreResponse.builder()
			.storeId(result.storeId())
			.name(result.name())
			.averageRating(result.averageRating())
			.reviewCount(result.reviewCount())
			.imageUrl(result.imageUrl())
			.status(result.status())
			.build();
	}
}
