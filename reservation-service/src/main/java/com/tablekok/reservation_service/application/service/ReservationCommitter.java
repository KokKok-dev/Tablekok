package com.tablekok.reservation_service.application.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.reservation_service.application.dto.command.CreateReservationCommand;
import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.service.ReservationDomainService;
import com.tablekok.reservation_service.global.annotation.DistributedLock;

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
