package com.tablekok.store_service.presentation.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

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
}
