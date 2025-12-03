package com.tablekok.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.review_service.application.client.ReservationClient;
import com.tablekok.review_service.application.client.dto.GetReservationResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationClientImpl implements ReservationClient {
	private final ReservationFeignClient reservationFeignClient;

	@Override
	public GetReservationResponse getReservation(UUID reservationId) {
		return reservationFeignClient.getReservation(reservationId);
	}
}
