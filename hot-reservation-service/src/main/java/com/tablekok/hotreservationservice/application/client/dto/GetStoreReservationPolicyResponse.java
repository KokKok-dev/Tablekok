package com.tablekok.hotreservationservice.application.client.dto;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.domain.vo.StoreReservationPolicy;

public record GetStoreReservationPolicyResponse(
	UUID policyId,
	UUID storeId,

	int monthlyOpenDay,
	LocalTime openTime,

	int reservationInterval,
	LocalTime dailyReservationStartTime,
	LocalTime dailyReservationEndTime,

	int minHeadcount,
	int maxHeadcount,

	boolean isDepositRequired,
	int depositAmount,
	boolean isActive
) {
	public static StoreReservationPolicy toVo(GetStoreReservationPolicyResponse response) {
		return StoreReservationPolicy.of(response.isActive, response.maxHeadcount, response.minHeadcount,
			response.monthlyOpenDay,
			response.openTime);
	}
}
