package com.tablekok.reservation_service.application.service.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.client.SearchClient;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerStrategy implements RoleStrategy {
	private final ReservationRepository reservationRepository;
	private final SearchClient searchClient;

	public Boolean supports(String role) {
		return role.equals("OWNER");
	}

	public void cancelReservation(UUID userId, UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		// 삭제할 예약의 음식점이 본인 음식점인지(= 본인 소유의 음식점 예약을 삭제하는지)

		if (!searchClient.checkStoreOwner(userId, findReservation.getStoreId())) {
			throw new AppException(ReservationErrorCode.FORBIDDEN_STORE_ACCESS);
		}

		findReservation.reject();
	}

}
