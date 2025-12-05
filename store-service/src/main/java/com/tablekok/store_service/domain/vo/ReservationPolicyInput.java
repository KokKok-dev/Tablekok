package com.tablekok.store_service.domain.vo;

import java.time.LocalTime;

import lombok.Builder;

@Builder
public record ReservationPolicyInput(
	int monthlyOpenDay,
	LocalTime openTime,
	int reservationInterval,
	LocalTime dailyReservationStartTime,
	LocalTime dailyReservationEndTime,
	int minHeadcount,
	int maxHeadcount,
	boolean isDepositRequired,
	int depositAmount
) {
}
