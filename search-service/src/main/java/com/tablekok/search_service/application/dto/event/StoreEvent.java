package com.tablekok.search_service.application.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.tablekok.search_service.domain.dto.StoreUpdateCommand;

public record StoreEvent(
	String operationType,
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
	List<String> categories,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy,
	LocalDateTime deletedAt,
	UUID deletedBy
) {
	public StoreUpdateCommand toCommand() {
		return StoreUpdateCommand.builder()
			.name(name)
			.storeStatus(storeStatus)
			.address(address)
			.phoneNumber(phoneNumber)
			.latitude(latitude)
			.longitude(longitude)
			.totalCapacity(totalCapacity)
			.isHot(isHot)
			.imageUrl(imageUrl)
			.description(description)
			.turnoverRateMinutes(turnoverRateMinutes)
			.waitingOpenTime(waitingOpenTime)
			.reservationOpenTime(reservationOpenTime)
			.categories(categories)
			.createdAt(createdAt)
			.createdBy(createdBy)
			.updatedAt(updatedAt)
			.updatedBy(updatedBy)
			.deletedAt(deletedAt)
			.deletedBy(deletedBy)
			.build();
	}
}
