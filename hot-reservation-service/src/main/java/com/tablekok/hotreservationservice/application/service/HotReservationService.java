package com.tablekok.hotreservationservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.hotreservationservice.application.client.StoreClient;
import com.tablekok.hotreservationservice.application.client.dto.GetStoreReservationPolicyResponse;
import com.tablekok.hotreservationservice.application.dto.command.CreateReservationCommand;
import com.tablekok.hotreservationservice.application.dto.result.CreateReservationResult;
import com.tablekok.hotreservationservice.domain.entity.Reservation;
import com.tablekok.hotreservationservice.domain.repository.ReservationRepository;
import com.tablekok.hotreservationservice.domain.service.ReservationDomainService;
import com.tablekok.hotreservationservice.domain.vo.StoreReservationPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotReservationService {
	private final ReservationDomainService reservationDomainService;
	private final StoreClient storeClient;
	private final ReservationRepository reservationRepository;

	// 예약 생성(접수)
	@Transactional
	public CreateReservationResult createReservation(CreateReservationCommand command) {

		// 생성 전 검증
		createValidate(command);

		// 생성
		Reservation newReservation = Reservation.create(
			command.userId(),
			command.storeId(),
			command.reservationDateTime(),
			command.headcount(),
			command.deposit()
		);

		// 저장
		reservationRepository.save(newReservation);

		return CreateReservationResult.of(newReservation);
	}

	// 생성 전 검증
	private void createValidate(CreateReservationCommand command) {
		// 도메인 서비스에서 검증하는게 맞나
		// 인기 음식점의 요청인지 확인
		List<UUID> hotStores = storeClient.getHotStores();
		reservationDomainService.validateHotStore(
			hotStores,
			command.storeId()
		);

		// 그 시간대 예약이 있는지
		reservationDomainService.validateDuplicateReservation(
			command.storeId(),
			command.reservationDateTime()
		);

		// 예약할 음식점의 예약 정책에 준수하는지
		StoreReservationPolicy policy = GetStoreReservationPolicyResponse.toVo(
			storeClient.getStoreReservationPolicy(command.storeId()));
		reservationDomainService.validateStoreReservationPolicy(
			command.headcount(),
			command.reservationDateTime(),
			policy
		);
	}
}
