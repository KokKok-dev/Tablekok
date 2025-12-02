package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.store_service.presentation.dto.request.CreateReservationPolicyRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateReservationPolicyRequest;
import com.tablekok.store_service.presentation.dto.response.GetReservationPolicyResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/stores/{storeId}/reservation-policy")
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

	@GetMapping
	public ResponseEntity<ApiResponse<GetReservationPolicyResponse>> getReservationPolicy(
		@PathVariable UUID storeId
	) {
		LocalTime openTime = LocalTime.of(10, 0); // 10:00
		LocalTime dailyStart = LocalTime.of(12, 0); // 12:00
		LocalTime dailyEnd = LocalTime.of(21, 0); // 21:00

		GetReservationPolicyResponse responseDto = new GetReservationPolicyResponse(
			UUID.fromString("8b5182e7-9d7a-4c2f-b4c6-e6d8a7f9c2d1"),
			storeId,
			15,
			openTime,
			30,
			dailyStart,
			dailyEnd,
			2,
			8,
			true,
			15000,
			true
		);
		return ResponseEntity.ok()
			.body(ApiResponse.success("예약정책 조회 성공", responseDto, HttpStatus.OK));
	}

	@PutMapping
	public ResponseEntity<ApiResponse<Void>> updateReservationPolicy(
		@PathVariable UUID storeId,
		@Valid @RequestBody UpdateReservationPolicyRequest requestDto
	) {
		// 날짜예약 정책 정보 수정
		return ResponseEntity.ok()
			.body(ApiResponse.success("예약정책 정보 변경 성공", HttpStatus.OK));
	}

}
