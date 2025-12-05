package com.tablekok.store_service.application.dto.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

import lombok.Builder;

@Builder
public record CreateStoreCommand(
	UUID ownerId,
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
	List<CreateOperatingHourCommand> operatingHours
) {

	public Store toEntity() {
		return Store.of(ownerId, name, phoneNumber, address, latitude, longitude, description, totalCapacity,
			turnoverRateMinutes, imageUrl);
	}

}
