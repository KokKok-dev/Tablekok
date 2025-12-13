package com.tablekok.hotreservationservice.application.client.dto;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.hotreservationservice.domain.vo.ReservationPolicy;

public record GetStoreReservationPolicyResponse(
	UUID policyId,
	UUID storeId,

	int monthlyOpenDay,
	LocalTime openTime,

	int reservationInterval,
	LocalTime dailyReservationStartTime,
	LocalTime dailyReservationEndTime,

	int minHeadCount,
	int maxHeadcount,

	boolean isDepositRequired,
	int depositAmount,
	boolean isActive
) {
	public static ReservationPolicy toVo(GetStoreReservationPolicyResponse response) {
		return ReservationPolicy.of(response.isActive, response.maxHeadcount, response.minHeadCount,
			response.monthlyOpenDay,
			response.openTime);
	}
}
