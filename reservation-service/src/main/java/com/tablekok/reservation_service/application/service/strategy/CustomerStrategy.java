package com.tablekok.reservation_service.application.service.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerStrategy implements RoleStrategy {
	private final ReservationRepository reservationRepository;

	public Boolean supports(String role) {
		return role.equals("CUSTOMER");
	}

	public void cancelReservation(UUID userId, UUID reservationId) {
		Reservation findReservation = reservationRepository.findByIdAndUserId(reservationId, userId);
		findReservation.cancel();
	}

}
