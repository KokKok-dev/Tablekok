package com.tablekok.reservation_service.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.vo.ReservationPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {
	private final ReservationRepository reservationRepository;

	// 예약 정책 검증
	public void validateReservation(Reservation reservation, ReservationPolicy policy) {

		// 음식점이 예약을 허용하는지
		if (!policy.getEnable()) {
			throw new AppException(ReservationDomainErrorCode.STORE_RESERVATION_DISABLED);
		}

		// 인원수가 정책에 준수하는지
		checkHeadcount(reservation.getHeadcount(), policy);

		// 예약 가능한 달인지
		validateMonthRule(reservation, policy);

	}

	// 중복 예약인지
	@Transactional(readOnly = true)
	public void checkDuplicateReservation(Reservation reservation) {
		boolean exists = reservationRepository.existsByStoreIdAndReservationDateTimeReservationDateAndReservationDateTimeReservationTime(
			reservation.getStoreId(),
			reservation.getReservationDateTime().getReservationDate(),
			reservation.getReservationDateTime().getReservationTime()
		);

		if (exists) {
			throw new AppException(ReservationDomainErrorCode.DUPLICATE_RESERVATION_TIME);
		}
	}

	// 인원수 체크
	public void checkHeadcount(Integer headcount, ReservationPolicy policy) {
		if (headcount > policy.getMaxPeople()) {
			throw new AppException(ReservationDomainErrorCode.INVALID_RESERVATION_POLICY);
		}
		if (headcount < policy.getMinPeople()) {
			throw new AppException(ReservationDomainErrorCode.INVALID_RESERVATION_POLICY);
		}
	}

	// 예약 가능한 달인지 검증
	private void validateMonthRule(Reservation reservation, ReservationPolicy policy) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate reservationDate = reservation.getReservationDateTime().getReservationDate();

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
	private void validateOpenTime(ReservationPolicy policy, LocalDateTime now) {
		LocalDate openDate = policy.getOpenDate();
		LocalTime openTime = policy.getOpenTime();

		LocalDateTime openDateTime = LocalDateTime.of(openDate, openTime);

		if (now.isBefore(openDateTime)) {
			throw new AppException(ReservationDomainErrorCode.RESERVATION_NOT_OPENED_YET);
		}
	}
}
