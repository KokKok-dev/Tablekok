package com.tablekok.hotreservationservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.hotreservationservice.application.client.StoreClient;
import com.tablekok.hotreservationservice.application.client.dto.GetStoreReservationPolicyResponse;
import com.tablekok.hotreservationservice.application.dto.command.CreateReservationCommand;
import com.tablekok.hotreservationservice.application.dto.result.CreateReservationResult;
import com.tablekok.hotreservationservice.domain.service.ReservationDomainService;
import com.tablekok.hotreservationservice.domain.vo.StoreReservationPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotReservationService {
	private final ReservationDomainService reservationDomainService;
	private final StoreClient storeClient;
	private final ReservationCommitter reservationCommitter;

	// 예약 생성(접수)
	public CreateReservationResult createReservation(CreateReservationCommand command) {
		// 락 밖: 외부 호출이 필요한 사전 검증 (락 보유시간 최소화)
		validateBeforeLock(command);

		// 락 안: 중복 검증 + 저장 (분산락 + 트랜잭션)
		return reservationCommitter.commit(command);
	}

	// 락 밖 사전 검증 (인기 음식점 여부 / 예약 정책) — 외부(store-service) 호출
	private void validateBeforeLock(CreateReservationCommand command) {
		// 인기 음식점의 요청인지 확인
		List<UUID> hotStores = storeClient.getHotStores();
		reservationDomainService.validateHotStore(
			hotStores,
			command.storeId()
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
