package com.tablekok.store_service.application.dto.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

import lombok.Builder;

@Builder
public record GetStoreResult(
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
	List<String> categoryNames,
	List<OperatingHourResult> operatingHours
) {

	public static GetStoreResult of(Store store, List<String> categoryNames) {
		List<OperatingHourResult> operatingHourResults = store.getOperatingHours().stream()
			.map(OperatingHourResult::from)
			.toList();

		return GetStoreResult.builder()
			.storeId(store.getId())
			.name(store.getName())
			.phoneNumber(store.getPhoneNumber())
			.address(store.getAddress())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.description(store.getDescription())
			.totalCapacity(store.getTotalCapacity())
			.turnoverRateMinutes(store.getTurnoverRateMinutes())
			.imageUrl(store.getImageUrl())
			.categoryNames(categoryNames)
			.operatingHours(operatingHourResults)
			.build();
	}
}
