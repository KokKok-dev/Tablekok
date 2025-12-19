package com.tablekok.store_service.presentation.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tablekok.store_service.application.dto.result.GetStoreReservationPolicyResult;

import lombok.Builder;

@Builder
public record GetStoreReservationPolicyResponse(
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

	public static GetStoreReservationPolicyResponse from(GetStoreReservationPolicyResult result) {
		return GetStoreReservationPolicyResponse.builder()
			.policyId(result.policyId())
			.storeId(result.storeId())
			.monthlyOpenDay(result.monthlyOpenDay())
			.openTime(result.openTime())
			.reservationInterval(result.reservationInterval())
			.dailyReservationStartTime(result.dailyReservationStartTime())
			.dailyReservationEndTime(result.dailyReservationEndTime())
			.minHeadCount(result.minHeadCount())
			.maxHeadcount(result.maxHeadcount())
			.isDepositRequired(result.isDepositRequired())
			.depositAmount(result.depositAmount())
			.isActive(result.isActive())
			.build();
	}
}
