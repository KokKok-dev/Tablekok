package com.tablekok.store_service.application.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

import lombok.Builder;

@Builder
public record StoreEvent(
	String operationType,      // "CREATE", "UPDATE", "DELETE"
	UUID storeId,
	String name,
	String storeStatus,
	String address,
	String phoneNumber,
	BigDecimal latitude,
	BigDecimal longitude,
	Integer totalCapacity,
	Boolean isHot,
	String imageUrl,
	String description,
	Integer turnoverRateMinutes,
	LocalTime waitingOpenTime,
	LocalTime reservationOpenTime,
	List<String> categoryIds,
	List<String> categories,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy,
	LocalDateTime deletedAt,
	UUID deletedBy
) {

	public static StoreEvent of(Store store, String operationType, List<String> categoryNames) {
		return StoreEvent.builder()
			.operationType(operationType)
			.storeId(store.getId())
			.name(store.getName())
			.storeStatus(store.getStatus().name())
			.address(store.getAddress())
			.phoneNumber(store.getPhoneNumber())
			.latitude(store.getLatitude())
			.longitude(store.getLongitude())
			.totalCapacity(store.getTotalCapacity())
			.isHot(store.getIsHot())
			.imageUrl(store.getImageUrl())
			.description(store.getDescription())
			.turnoverRateMinutes(store.getTurnoverRateMinutes())
			.waitingOpenTime(store.getWaitingOpenTime())
			.reservationOpenTime(store.getReservationOpenTime())
			// List<UUID> -> List<String> 변환
			.categoryIds(
				store.getCategoryIds().stream()
					.map(UUID::toString)
					.toList()
			)
			.categories(categoryNames)
			.createdAt(store.getCreatedAt())
			.createdBy(store.getCreatedBy())
			.updatedAt(store.getUpdatedAt())
			.updatedBy(store.getUpdatedBy())
			.deletedAt(store.getDeletedAt())
			.deletedBy(store.getDeletedBy())
			.build();
	}
}
