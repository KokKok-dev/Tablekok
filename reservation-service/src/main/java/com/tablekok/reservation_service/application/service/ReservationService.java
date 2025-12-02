package com.tablekok.reservation_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.reservation_service.application.dto.param.CreateReservationParam;
import com.tablekok.reservation_service.application.port.SearchPort;
import com.tablekok.reservation_service.application.port.dto.response.GetReservationPolicyResponse;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.service.ReservationDomainService;
import com.tablekok.reservation_service.domain.vo.ReservationPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ReservationDomainService reservationDomainService;
	private final SearchPort searchPort;

	// 예약 생성(접수)
	@Transactional
	public UUID createReservation(CreateReservationParam param) {
		Reservation newReservation = param.toEntity();

		// 그 시간대 예약이 있는지
		reservationDomainService.checkDuplicateReservation(newReservation);

		// 인기식당 리스트 조회
		List<UUID> hotStoreList = searchPort.getHotStores();

		// 음식점 정책 조회
		ReservationPolicy policy = GetReservationPolicyResponse.toVo(
			searchPort.getReservationPolicy(newReservation.getStoreId()));

		// 예약 정책 검증 TODO 내부호출 구현 후 테스트
		reservationDomainService.validateReservation(newReservation, hotStoreList, policy);

		// 저장
		reservationRepository.save(newReservation);

		return newReservation.getId();
	}

}
