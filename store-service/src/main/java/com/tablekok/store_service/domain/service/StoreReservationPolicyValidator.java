package com.tablekok.store_service.domain.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;
import com.tablekok.store_service.domain.vo.StoreReservationPolicyInput;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreReservationPolicyValidator {

	public void validate(StoreReservationPolicyInput input, Store store) {
		// 1. 예약 가능한 간격 분 10/ 15 /20/30/60/120 입력인지 확인
		List<Integer> validIntervals = List.of(10, 15, 20, 30, 60, 120);
		if (!validIntervals.contains(input.reservationInterval())) {
			throw new AppException(StoreDomainErrorCode.INVALID_RESERVATION_INTERVAL);
		}

		// 2. dailyReservationEndTime이 dailyReservationStartTime보다 이후 시간인지 확인
		if (!input.dailyReservationEndTime().isAfter(input.dailyReservationStartTime())) {
			throw new AppException(StoreDomainErrorCode.INVALID_TIME_RANGE);
		}

		// 3. maxHeadcount가 minHeadcount보다 크거나 같은지 확인
		if (input.maxHeadcount() < input.minHeadcount()) {
			throw new AppException(StoreDomainErrorCode.INVALID_HEADCOUNT_RANGE);
		}

		// 4. is_deposit_required가 true인 경우 deposit_amount는 0보다 큰가
		if (input.isDepositRequired() && input.depositAmount() <= 0) {
			throw new AppException(StoreDomainErrorCode.INVALID_DEPOSIT_AMOUNT);
		}

		validateTimeConsistency(
			input.dailyReservationStartTime(),
			input.dailyReservationEndTime(),
			input.reservationInterval(),
			store
		);
	}

	/**
	 * Store의 OperatingHour 정보와 예약 정책 시간 간의 일관성을 검증
	 */
	private void validateTimeConsistency(
		LocalTime dailyReservationStartTime,
		LocalTime dailyReservationEndTime,
		int reservationInterval,
		Store store
	) {
		// 5. dailyReservationEndTime과 dailyReservationStartTime 사이의 시간이 interval보다 충분히 긴지 확인
		Duration duration = Duration.between(dailyReservationStartTime, dailyReservationEndTime);
		if (duration.toMinutes() < reservationInterval) {
			throw new AppException(StoreDomainErrorCode.INSUFFICIENT_TIME_SLOT);
		}

		// 예약 시작/종료 시간이 모든 운영 시간 범위 내에 있는지 확인
		for (OperatingHour hour : store.getOperatingHours()) {
			if (hour.isClosed()) {
				continue;
			}

			// 예약 시작 시간이 운영 시작 시간보다 이전인지 확인
			if (dailyReservationStartTime.isBefore(hour.getOpenTime())) {
				throw new AppException(StoreDomainErrorCode.RESERVATION_TIME_BEFORE_OPERATING_OPEN);
			}

			// 예약 종료 시간이 운영 종료 시간보다 이후인지 확인
			if (dailyReservationEndTime.isAfter(hour.getCloseTime())) {
				throw new AppException(StoreDomainErrorCode.RESERVATION_TIME_AFTER_OPERATING_CLOSE);
			}
		}
	}
}
