package com.tablekok.store_service.presentation.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record GetReservationPolicyResponse(
	UUID policyId,
	UUID storeId,

	int monthlyOpenDay,
	@JsonFormat(pattern = "HH:mm")
	LocalTime openTime,
	int reservationInterval,

	@JsonFormat(pattern = "HH:mm")
	LocalTime dailyReservationStartTime,
	@JsonFormat(pattern = "HH:mm")
	LocalTime dailyReservationEndTime,

	int minHeadCount,
	int maxHeadcount,

	boolean isDepositRequired,
	int depositAmount,
	boolean isActive

) {
	
	public static GetReservationPolicyResponse from() {
		return GetReservationPolicyResponse.builder()
			.policyId(UUID.randomUUID())
			.storeId(UUID.randomUUID())
			.monthlyOpenDay(15)
			.openTime(LocalTime.of(10, 0))
			.reservationInterval(30)
			.dailyReservationStartTime(LocalTime.of(12, 0))
			.dailyReservationEndTime(LocalTime.of(21, 0))
			.minHeadCount(2)
			.maxHeadcount(8)
			.isDepositRequired(true)
			.depositAmount(15000)
			.isActive(true)
			.build();
	}
}
