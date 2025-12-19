package com.tablekok.store_service.application.dto.result;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tablekok.store_service.domain.entity.StoreReservationPolicy;

import lombok.Builder;

@Builder
public record GetStoreReservationPolicyResult(
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

	public static GetStoreReservationPolicyResult from(StoreReservationPolicy policy) {
		return GetStoreReservationPolicyResult.builder()
			.policyId(policy.getId())
			.storeId(policy.getId())
			.monthlyOpenDay(policy.getMonthlyOpenDay())
			.openTime(policy.getOpenTime())
			.reservationInterval(policy.getReservationInterval())
			.dailyReservationStartTime(policy.getDailyReservationStartTime())
			.dailyReservationEndTime(policy.getDailyReservationEndTime())
			.minHeadCount(policy.getMinHeadcount())
			.maxHeadcount(policy.getMaxHeadcount())
			.isDepositRequired(policy.isDepositRequired())
			.depositAmount(policy.getDepositAmount())
			.isActive(policy.isActive())
			.build();
	}
}
