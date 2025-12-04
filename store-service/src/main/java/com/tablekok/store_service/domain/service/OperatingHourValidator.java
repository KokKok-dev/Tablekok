package com.tablekok.store_service.domain.service;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;
import com.tablekok.store_service.domain.vo.OperatingHourData;

@Service
public class OperatingHourValidator {

	public void validateOperatingHours(List<OperatingHourData> hours) {
		Set<DayOfWeek> days = new HashSet<>();
		for (OperatingHourData hour : hours) {

			hour.validate();

			// 요일 중복 검사
			if (!days.add(hour.getDayOfWeek())) {
				throw new AppException(StoreDomainErrorCode.DUPLICATE_OPERATING_DAY);
			}

		}

		// 7개 요일 중 하나라도 누락되었다면 예외 발생
		if (days.size() != 7) {
			throw new AppException(StoreDomainErrorCode.MISSING_ALL_OPERATING_DAYS);
		}
	}
}
