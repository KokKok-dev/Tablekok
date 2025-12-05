package com.tablekok.store_service.presentation.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.dto.result.OperatingHourResult;

import lombok.Builder;

@Builder
public record CreateStoreResponse(
	UUID storeId,
	String name,
	String phoneNumber,
	String address,
	BigDecimal latitude,
	BigDecimal longitude,
	String description,
	Integer totalCapacity,
	Integer turnoverRateMinutes,
	String imageUrl,
	List<UUID> categoryIds,

	List<OperatingHourResult> operatingHours
) {
	public static CreateStoreResponse from(CreateStoreResult result) {
		return CreateStoreResponse.builder()
			.storeId(result.storeId())
			.name(result.name())
			.phoneNumber(result.phoneNumber())
			.address(result.address())
			.latitude(result.latitude())
			.longitude(result.longitude())
			.description(result.description())
			.totalCapacity(result.totalCapacity())
			.turnoverRateMinutes(result.turnoverRateMinutes())
			.imageUrl(result.imageUrl())
			.categoryIds(result.categoryIds())
			.operatingHours(result.operatingHours())
			.build();
	}
}
