package com.tablekok.store_service.domain.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.exception.StoreErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHourData {
	private DayOfWeek dayOfWeek;
	private LocalTime openTime;
	private LocalTime closeTime;
	private boolean isClosed;

	public static OperatingHourData of(
		DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean isClosed
	) {
		return OperatingHourData.builder()
			.dayOfWeek(dayOfWeek)
			.openTime(openTime)
			.closeTime(closeTime)
			.isClosed(isClosed)
			.build();
	}

	public void validate() {
		// 1. isClosed가 true일 경우 시간 필드는 반드시 null이어야 합니다.
		if (isClosed) {
			if (openTime != null || closeTime != null) {
				throw new AppException(StoreErrorCode.INVALID_CLOSED_TIME);
			}
		}
		// 2. isClosed가 false일 경우 시간 필드는 반드시 존재해야 합니다.
		else {
			if (openTime == null || closeTime == null) {
				throw new AppException(StoreErrorCode.MISSING_OPERATING_TIME);
			}
			// 3. closeTime 이 openTime 이후인지 검증
			if (openTime.isAfter(closeTime)) {
				throw new AppException(StoreErrorCode.INVALID_TIME_RANGE);
			}
		}
	}
}

