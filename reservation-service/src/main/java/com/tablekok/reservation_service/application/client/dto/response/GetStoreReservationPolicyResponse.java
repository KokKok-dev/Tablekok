package com.tablekok.reservation_service.application.client.dto.response;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.reservation_service.domain.vo.StoreReservationPolicy;

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
	public static StoreReservationPolicy toVo(GetStoreReservationPolicyResponse response) {
		return StoreReservationPolicy.of(response.isActive, response.maxHeadcount, response.minHeadCount,
			response.monthlyOpenDay,
			response.openTime);
	}
}
