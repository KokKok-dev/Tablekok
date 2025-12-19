package com.tablekok.reservation_service.application.service.strategy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.client.StoreClient;
import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnerStrategy implements RoleStrategy {
	private final ReservationRepository reservationRepository;
	private final StoreClient storeClient;

	public Boolean supports(UserRole role) {
		return role == UserRole.OWNER;
	}

	public void cancelReservation(UUID userId, UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		// 삭제할 예약의 음식점이 본인 음식점인지(= 본인 소유의 음식점 예약을 삭제하는지)

		OwnerVerificationRequest request = OwnerVerificationRequest.of(findReservation.getStoreId(), userId);
		if (!storeClient.checkStoreOwner(request)) {
			throw new AppException(ReservationErrorCode.FORBIDDEN_STORE_ACCESS);
		}

		findReservation.reject();
	}

}
