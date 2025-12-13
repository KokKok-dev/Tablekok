package com.tablekok.hotreservationservice.domain.vo;

import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class StoreReservationPolicy {
	private boolean isActive;       // 예약 가능 여부
	private int maxHeadcount;        // 예약 최대 인원
	private int minHeadCount;        // 예약 최소 인원
	private int monthlyOpenDay;     // 다음 달 예약이 풀리는 일
	private LocalTime openTime;        // 다음 달 예약이 풀리는 시간

	public static StoreReservationPolicy of(
		boolean isActive, int maxHeadcount, int minHeadCount, int monthlyOpenDay, LocalTime openTime) {

		return StoreReservationPolicy.builder()
			.isActive(isActive)
			.maxHeadcount(maxHeadcount)
			.minHeadCount(minHeadCount)
			.monthlyOpenDay(monthlyOpenDay)
			.openTime(openTime)
			.build();
	}
}
