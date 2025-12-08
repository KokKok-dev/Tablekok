package com.tablekok.store_service.application.dto.command;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreReservationPolicy;
import com.tablekok.store_service.domain.vo.StoreReservationPolicyInput;

import lombok.Builder;

@Builder
public record CreateStoreReservationPolicyCommand(

	UUID storeId,

	Integer monthlyOpenDay,

	LocalTime openTime,

	Integer reservationInterval,

	LocalTime dailyReservationStartTime,

	LocalTime dailyReservationEndTime,

	Integer minHeadCount,

	Integer maxHeadcount,

	Boolean isDepositRequired,

	Integer depositAmount,

	Boolean isActive
) {

	public StoreReservationPolicy toEntity(Store store) {
		return StoreReservationPolicy.of(store, monthlyOpenDay, openTime, reservationInterval,
			dailyReservationStartTime,
			dailyReservationEndTime,
			minHeadCount, maxHeadcount, isDepositRequired, depositAmount, isActive);
	}

	public StoreReservationPolicyInput toVo() {
		return StoreReservationPolicyInput.builder()
			.monthlyOpenDay(monthlyOpenDay)
			.openTime(openTime)
			.reservationInterval(reservationInterval)
			.dailyReservationStartTime(dailyReservationStartTime)
			.dailyReservationEndTime(dailyReservationEndTime)
			.minHeadcount(minHeadCount)
			.maxHeadcount(maxHeadcount)
			.isDepositRequired(isDepositRequired)
			.depositAmount(depositAmount)
			.build();
	}
}
