package com.tablekok.hotreservationservice.application.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.hotreservationservice.application.dto.command.CreateReservationCommand;
import com.tablekok.hotreservationservice.application.dto.result.CreateReservationResult;
import com.tablekok.hotreservationservice.domain.entity.Reservation;
import com.tablekok.hotreservationservice.domain.repository.ReservationRepository;
import com.tablekok.hotreservationservice.domain.service.ReservationDomainService;
import com.tablekok.hotreservationservice.global.annotation.DistributedLock;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationCommitter {

	private final ReservationDomainService reservationDomainService;
	private final ReservationRepository reservationRepository;

	// 분산락 임계구역 — 중복 검증 + 저장만 수행 (외부 호출은 락 밖에서 끝낸 상태)
	@Transactional
	@DistributedLock(key = "'reservation:' + #command.storeId() + ':' + #command.reservationDateTime().getReservationDate() + ':' + #command.reservationDateTime().getReservationTime()")
	public CreateReservationResult commit(CreateReservationCommand command) {
		// 활성 예약 중 동일 슬롯이 있는지
		reservationDomainService.validateDuplicateReservation(
			command.storeId(),
			command.reservationDateTime()
		);

		Reservation newReservation = Reservation.create(
			command.userId(),
			command.storeId(),
			command.reservationDateTime(),
			command.headcount(),
			command.deposit()
		);

		reservationRepository.save(newReservation);
		return CreateReservationResult.of(newReservation);
	}
}
