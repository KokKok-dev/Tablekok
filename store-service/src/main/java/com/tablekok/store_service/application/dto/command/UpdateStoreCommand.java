package com.tablekok.store_service.application.dto.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UpdateStoreCommand(
	UUID ownerId,
	String userRole,
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
	List<CreateOperatingHourCommand> operatingHours
) {

}
