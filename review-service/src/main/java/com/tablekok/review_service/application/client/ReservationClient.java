package com.tablekok.review_service.application.client;

import java.util.UUID;

import com.tablekok.review_service.application.client.dto.GetReservationResponse;

public interface ReservationClient {

	GetReservationResponse getReservation(UUID reservationId);
}
