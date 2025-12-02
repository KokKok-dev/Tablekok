package com.tablekok.reservation_service.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

	// 예약 검증
	public void validateReservation(Reservation reservation, List<UUID> hotStoreList, ReservationPolicy policy) {
		// 인기 음식점 예약 요청인지 확인
		if (reservation.checkHotStore(hotStoreList)) {
			throw new AppException(ReservationDomainErrorCode.HOT_STORE_RESERVATION_NOT_ALLOWED);
		}

		// 음식점의 예약 정책 검증
		// 음식점이 예약을 허용하는지
		if (!policy.getEnable()) {
			throw new AppException(ReservationDomainErrorCode.STORE_RESERVATION_DISABLED);
		}

		// 인원수가 정책에 준수하는지
		checkHeadcount(reservation.getHeadcount(), policy);

		// 예약 가능한 시간대인지
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime targetDateTime = LocalDateTime.of(
			reservation.getReservationDateTime().getReservationDate(),
			reservation.getReservationDateTime().getReservationTime());

		// 1. 과거는 예약 불가
		if (targetDateTime.isBefore(now)) {
			throw new AppException(ReservationDomainErrorCode.PAST_RESERVATION_NOT_ALLOWED);
		}

		// 2. 다음 달 예약인지 판별
		LocalDate today = now.toLocalDate();
		int currentMonth = today.getMonthValue();
		int targetMonth = targetDateTime.getMonthValue();
		boolean isNextMonth = targetMonth == currentMonth + 1 || (currentMonth == 12 && targetMonth == 1);

		if (!isNextMonth) {
			throw new AppException(ReservationDomainErrorCode.RESERVATION_NOT_AVAILABLE_MONTH);
		}

		// 3. 다음 달 예약이면 openDate + openTime 검증. 예약 오픈 시간을 LocalDateTime으로 만들고 현재 시간과 비교
		LocalDateTime openDateTime = LocalDateTime.of(
			today.withDayOfMonth(policy.getOpenDate().getMonthValue()),
			policy.getOpenTime()
		);

		if (now.isBefore(openDateTime)) {
			throw new AppException(ReservationDomainErrorCode.RESERVATION_NOT_OPENED_YET);
		}

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

}
