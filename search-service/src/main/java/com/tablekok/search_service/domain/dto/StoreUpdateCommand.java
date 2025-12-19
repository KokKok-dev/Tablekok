package com.tablekok.search_service.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record StoreUpdateCommand(
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
}
