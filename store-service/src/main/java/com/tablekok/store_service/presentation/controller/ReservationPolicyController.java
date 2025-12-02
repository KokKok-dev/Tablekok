package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.store_service.presentation.dto.request.CreateReservationPolicyRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/stores/reservation-policy")
public class ReservationPolicyController {

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createReservationPolicy(
		@Valid @RequestBody CreateReservationPolicyRequest requestDto
	) {
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{reservationPolicyId}")
			.buildAndExpand(UUID.randomUUID())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("예약정책 생성 성공", HttpStatus.CREATED));

	}
}
