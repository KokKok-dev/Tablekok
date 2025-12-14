package com.tablekok.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tablekok.review_service.application.client.dto.GetReservationResponse;

@FeignClient(name = "reservation-service")
public interface ReservationFeignClient {

	@GetMapping("/v1/reservations/{reservationId}")
	ResponseEntity<GetReservationResponse> getReservation(@PathVariable("reservationId") UUID reservationId);
}
