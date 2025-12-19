package com.tablekok.store_service.domain.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;
import com.tablekok.store_service.domain.vo.OperatingHourInput;

@Service
public class OperatingHourValidator {

	public void validateOperatingHourInputs(List<OperatingHourInput> inputs) {
		Set<DayOfWeek> days = new HashSet<>();
		for (OperatingHourInput input : inputs) {

			// 요일 중복 검사
			if (!days.add(input.dayOfWeek())) {
				throw new AppException(StoreDomainErrorCode.DUPLICATE_OPERATING_DAY);
			}
			validateTimes(input.openTime(), input.closeTime(), input.isClosed());

		}

		// 7개 요일 중 하나라도 누락되었다면 예외 발생
		if (days.size() != 7) {
			throw new AppException(StoreDomainErrorCode.MISSING_ALL_OPERATING_DAYS);
		}
	}

	public void validateTimes(LocalTime openTime, LocalTime closeTime, boolean isClosed) {
		if (isClosed) {
			// 1. 휴무일로 설정된 경우, 시간은 반드시 null이어야 합니다.
			if (openTime != null || closeTime != null) {
				throw new AppException(StoreDomainErrorCode.INVALID_CLOSED_TIME);
			}
		} else {
			// 2. 운영일로 설정된 경우, 시작 시간과 종료 시간은 반드시 필요합니다.
			if (openTime == null || closeTime == null) {
				throw new AppException(StoreDomainErrorCode.MISSING_OPERATING_TIME);
			}

			// 3. 운영 종료 시간은 운영 시작 시간 이후여야 합니다. (LocalTime 비교)
			if (closeTime.isBefore(openTime)) {
				throw new AppException(StoreDomainErrorCode.INVALID_TIME_RANGE);
			}
		}
	}
}
