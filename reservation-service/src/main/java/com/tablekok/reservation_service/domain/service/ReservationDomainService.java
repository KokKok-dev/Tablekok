package com.tablekok.reservation_service.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.domain.exception.ReservationDomainErrorCode;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;
import com.tablekok.reservation_service.domain.vo.StoreReservationPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {
	private final ReservationRepository reservationRepository;

	// 예약 정책 검증
	public void validateStoreReservationPolicy(Integer headcount, ReservationDateTime reservationDateTime,
		StoreReservationPolicy policy) {

		// 음식점이 예약을 허용하는지
		if (!policy.isActive()) {
			throw new AppException(ReservationDomainErrorCode.STORE_RESERVATION_DISABLED);
		}

		// 인원수가 정책에 준수하는지
		validateHeadcount(headcount, policy);

		// 예약 가능한 달인지 검증
		validateMonthRule(reservationDateTime, policy);
	}

	// 예약 가능한 달인지 검증
	private void validateMonthRule(ReservationDateTime reservationDateTime, StoreReservationPolicy policy) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate reservationDate = reservationDateTime.getReservationDate();

		// 현재 달과 예약한 달의 차이
		int diff = monthDiff(now.toLocalDate(), reservationDate);

		// 1) 현재 달(Diff = 0) → 항상 예약 가능
		if (diff == 0) {
			return;
		}

		// 2) 다음 달(Diff = 1) → openDate/openTime 검증 필요
		if (diff == 1) {
			validateOpenTime(policy, now);
			return;
		}

		// 3) 다다음달 이상(Diff >= 2) → 예약 거절
		throw new AppException(ReservationDomainErrorCode.RESERVATION_NOT_AVAILABLE_MONTH);
	}

	// 현재 달과 예약한 달의 차이 구하기
	private int monthDiff(LocalDate from, LocalDate to) {
		return (to.getYear() - from.getYear()) * 12
			+ (to.getMonthValue() - from.getMonthValue());
	}

	// 다음 달 예약일 때, 다음 달 예약이 가능한 날이 지났는지 확인
	private void validateOpenTime(StoreReservationPolicy policy, LocalDateTime now) {
		LocalDate openDate = LocalDate.now().withDayOfMonth(policy.getMonthlyOpenDay());
		LocalTime openTime = policy.getOpenTime();

		LocalDateTime openDateTime = LocalDateTime.of(openDate, openTime);

		if (now.isBefore(openDateTime)) {
			throw new AppException(ReservationDomainErrorCode.RESERVATION_NOT_OPENED_YET);
		}
	}

	// 중복 예약인지
	@Transactional(readOnly = true)
	public void validateDuplicateReservation(UUID storeId, ReservationDateTime reservationDateTime) {
		boolean exists = reservationRepository.existsByStoreIdAndReservationDateTime_ReservationDateAndReservationDateTime_ReservationTime(
			storeId,
			reservationDateTime.getReservationDate(),
			reservationDateTime.getReservationTime()
		);

		if (exists) {
			throw new AppException(ReservationDomainErrorCode.DUPLICATE_RESERVATION_TIME);
		}
	}

	// 인기 음식점의 예약이면 거절
	public void validateHotStore(List<UUID> hotStoreList, UUID storeId) {
		if (hotStoreList.contains(storeId)) {
			throw new AppException(ReservationDomainErrorCode.HOT_STORE_RESERVATION_NOT_ALLOWED);
		}
	}

	// 인원수 체크
	public void validateHeadcount(Integer headcount, StoreReservationPolicy policy) {
		if (headcount > policy.getMaxHeadcount()) {
			throw new AppException(ReservationDomainErrorCode.INVALID_RESERVATION_POLICY);
		}
		if (headcount < policy.getMinHeadCount()) {
			throw new AppException(ReservationDomainErrorCode.INVALID_RESERVATION_POLICY);
		}
	}

}
