package com.tablekok.store_service.application.dto.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;

import lombok.Builder;

@Builder
public record CreateStoreResult(
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

	public static CreateStoreResult of(Store store, List<OperatingHour> savedOperatingHours) {
		List<OperatingHourResult> operatingHourResults = savedOperatingHours.stream()
			.map(OperatingHourResult::from)
			.toList();

		return CreateStoreResult.builder()
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
			.categoryIds(store.getCategoryIds())
			.operatingHours(operatingHourResults)
			.build();
	}

}
