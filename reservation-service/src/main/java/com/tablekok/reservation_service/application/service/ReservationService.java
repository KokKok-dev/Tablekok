package com.tablekok.reservation_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.reservation_service.application.client.SearchClient;
import com.tablekok.reservation_service.application.dto.param.CreateReservationParam;
import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;
import com.tablekok.reservation_service.application.strategy.RoleStrategy;
import com.tablekok.reservation_service.application.strategy.StrategyFactory;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.service.ReservationDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ReservationDomainService reservationDomainService;
	private final SearchClient searchClient;
	private final StrategyFactory strategyFactory;

	// 예약 생성(접수)
	@Transactional
	public CreateReservationResult createReservation(CreateReservationParam param) {
		Reservation newReservation = param.toEntity();

		// 인기 음식점 조회 후 확인
		newReservation.validateHotStore(searchClient.getHotStores());

		// 과거 시간을 예약했는지
		newReservation.validateNotPast();

		// 그 시간대 예약이 있는지
		reservationDomainService.checkDuplicateReservation(newReservation);

		// 음식점 정책 조회 		TODO 내부호출 구현 후 테스트
		// ReservationPolicy policy = GetReservationPolicyResponse.toVo(
		// 	searchClient.getReservationPolicy(newReservation.getStoreId()));
		// 예약 정책 검증			TODO 내부호출 구현 후 테스트
		// reservationDomainService.validateReservation(newReservation, policy);

		// 저장
		reservationRepository.save(newReservation);

		return CreateReservationResult.of(newReservation);
	}

	// 예약 취소
	@Transactional
	public void cancelReservation(UUID userId, String userRole, UUID reservationId) {
		RoleStrategy strategy = strategyFactory.getStrategy(userRole); //TODO 유저 구현 후 Role값으로
		strategy.cancelReservation(userId, reservationId);
	}

}
