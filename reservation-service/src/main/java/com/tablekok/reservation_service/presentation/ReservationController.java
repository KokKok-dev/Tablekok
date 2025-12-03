package com.tablekok.reservation_service.presentation;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;
import com.tablekok.reservation_service.application.service.ReservationService;
import com.tablekok.reservation_service.presentation.dto.request.CreateReservationRequest;
import com.tablekok.reservation_service.presentation.dto.request.UpdateHeadcountRequest;
import com.tablekok.reservation_service.presentation.dto.response.CreateReservationResponse;
import com.tablekok.reservation_service.presentation.dto.response.GetReservationResponse;
import com.tablekok.reservation_service.presentation.dto.response.GetReservationsForCustomerResponse;
import com.tablekok.reservation_service.presentation.dto.response.GetReservationsForOwnerResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationController {
	private final ReservationService reservationService;

	// 예약 생성(예약 접수)
	@PostMapping
	public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(
		@Valid @RequestBody CreateReservationRequest request) {
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현

		CreateReservationResult result = reservationService.createReservation(request.toParam(userId));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{reservationId}")
			.buildAndExpand(result.reservationId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("예약 성공",
				CreateReservationResponse.fromResult(result),
				HttpStatus.CREATED));
	}

	// 단건 예약 조회(리뷰에서 호출 용도)
	@GetMapping("/{reservationId}")
	public GetReservationResponse getReservation(@PathVariable("reservationId") UUID reservationId) {
		return GetReservationResponse.fromResult(reservationService.getReservation(reservationId));
	}

	// 예약 인원수 변경
	@PatchMapping("/{reservationId}")
	public ResponseEntity<ApiResponse<Void>> updateHeadcount(@PathVariable("reservationId") UUID reservationId,
		@Valid @RequestBody UpdateHeadcountRequest request) {
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현

		reservationService.updateHeadcount(userId, reservationId, request.headcount());
		return ResponseEntity.ok(
			ApiResponse.success("예약 인원 변경 성공", HttpStatus.OK));
	}

	// 예약 취소. 고객, 오너 전략패턴 적용
	@PatchMapping("/{reservationId}/cancel")
	public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable("reservationId") UUID reservationId) {
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현
		String userRole = "OWNER";     //TODO 추후 유저id 구현

		reservationService.cancelReservation(userId, userRole, reservationId);
		return ResponseEntity.ok(
			ApiResponse.success("예약 취소 성공", HttpStatus.OK));
	}

	// 예약 노쇼(오너)
	@PatchMapping("/owner/{reservationId}/noshow")
	public ResponseEntity<ApiResponse<Void>> noShow(@PathVariable("reservationId") UUID reservationId) {
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현

		reservationService.noShow(userId, reservationId);
		return ResponseEntity.ok(
			ApiResponse.success("예약 노쇼(오너) 성공", HttpStatus.OK));
	}

	// 예약 조회(고객)
	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetReservationsForCustomerResponse>>> getReservationsForCustomer(
		Pageable pageable) {

		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현
		return ResponseEntity.ok(
			ApiResponse.success("예약 조회(고객) 성공",
				GetReservationsForCustomerResponse.fromResult(
					reservationService.getReservationsForCustomer(userId, pageable)),
				HttpStatus.OK));
	}

	// 예약 조회(오너)
	@GetMapping("/owner")
	public ResponseEntity<ApiResponse<Page<GetReservationsForOwnerResponse>>> getReservationsForOwner(
		@RequestParam UUID storeId, Pageable pageable) {

		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677"); //TODO 추후 유저id 구현
		return ResponseEntity.ok(
			ApiResponse.success("예약 조회(오너) 성공",
				GetReservationsForOwnerResponse.fromResult(
					reservationService.getReservationsForOwner(userId, storeId, pageable)),
				HttpStatus.OK));
	}

}
