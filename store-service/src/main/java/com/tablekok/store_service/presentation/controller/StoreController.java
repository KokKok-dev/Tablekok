package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.entity.UserRole;
import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.dto.result.GetStoreReservationPolicyResult;
import com.tablekok.store_service.application.service.StoreService;
import com.tablekok.store_service.presentation.dto.request.CreateStoreRequest;
import com.tablekok.store_service.presentation.dto.request.CreateStoreReservationPolicyRequest;
import com.tablekok.store_service.presentation.dto.request.UpdatePolicyStatusRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateStatusRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateStoreRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateStoreReservationPolicyRequest;
import com.tablekok.store_service.presentation.dto.response.CreateStoreResponse;
import com.tablekok.store_service.presentation.dto.response.GetStoreReservationPolicyResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateStoreResponse>> createStore(
		@Valid @RequestBody CreateStoreRequest request,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		// store 생성
		UUID ownerId = UUID.randomUUID(); // TODO: 사장님 ID 가져와야함

		CreateStoreResult result = storeService.createStore(request.toCommand(ownerId));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{storeId}")
			.buildAndExpand(result.storeId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("음식점 생성 성공", CreateStoreResponse.from(result), HttpStatus.CREATED));
	}

	@PatchMapping("/{storeId}")
	public ResponseEntity<ApiResponse<Void>> updateStore(
		@PathVariable UUID storeId,
		@Valid @RequestBody UpdateStoreRequest request,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		// store 생성
		UUID ownerId = UUID.randomUUID(); // TODO: 사장님 ID 가져와야함

		storeService.updateStore(request.toCommand(ownerId, storeId));

		return ResponseEntity.ok(
			ApiResponse.success("음식점 정보 수정 성공", HttpStatus.OK)
		);
	}

	@PatchMapping("/{storeId}/status")
	public ResponseEntity<ApiResponse<Void>> updateStatus(
		@PathVariable UUID storeId,
		@Valid @RequestBody UpdateStatusRequest request,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		// TODO: 추후 userRole 작업
		UserRole role = UserRole.fromName(userRole);
		// 음식점 상태 변경
		storeService.updateStatus(role, storeId, request.toCommand());

		return ResponseEntity.ok(
			ApiResponse.success("음식점 상태 수정 성공", HttpStatus.OK)
		);
	}

	@DeleteMapping("/{storeId}")
	public ResponseEntity<ApiResponse<Void>> deleteStore(
		@PathVariable UUID storeId,  // TODO : MASTER만 요청가능
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		// 음식점 삭제
		UUID deleterId = UUID.randomUUID();
		storeService.deleteStore(storeId, deleterId);

		return ResponseEntity.ok(
			ApiResponse.success("음식점 삭제 성공", HttpStatus.OK)
		);
	}

	/* =================== 음식점 날짜 예약 정책 ================ **/
	@PostMapping("/{storeId}/reservation-policy")
	public ResponseEntity<ApiResponse<Void>> createStoreReservationPolicy(
		@PathVariable UUID storeId,
		@Valid @RequestBody CreateStoreReservationPolicyRequest request
	) {
		storeService.createStoreReservationPolicy(storeId, request.toCommand(storeId));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{storeReservationPolicyId}")
			.buildAndExpand(UUID.randomUUID())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("예약정책 생성 성공", HttpStatus.CREATED));
	}

	@GetMapping("/{storeId}/reservation-policy")
	public ResponseEntity<ApiResponse<GetStoreReservationPolicyResponse>> getStoreReservationPolicy(
		@PathVariable UUID storeId
	) {
		GetStoreReservationPolicyResult result = storeService.getStoreReservationPolicy(storeId);
		GetStoreReservationPolicyResponse response = GetStoreReservationPolicyResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("예약정책 조회 성공", response, HttpStatus.OK));
	}

	@PutMapping("/{storeId}/reservation-policy")
	public ResponseEntity<ApiResponse<Void>> updateStoreReservationPolicy(
		@PathVariable UUID storeId,
		@Valid @RequestBody UpdateStoreReservationPolicyRequest request
	) {
		// 날짜예약 정책 정보 수정
		UUID ownerId = UUID.randomUUID(); // TODO: 사장님 ID 가져와야함

		storeService.updateStoreReservationPolicy(request.toCommand(ownerId, storeId));
		return ResponseEntity.ok()
			.body(ApiResponse.success("예약정책 정보 변경 성공", HttpStatus.OK));
	}

	@PatchMapping("/{storeId}/reservation-policy/status")
	public ResponseEntity<ApiResponse<Void>> updateStoreReservationPolicyStatus(
		@PathVariable UUID storeId,
		@Valid @RequestBody UpdatePolicyStatusRequest request
	) {
		// 날짜예약 정책 활성/비활성화
		UUID ownerId = UUID.randomUUID(); // TODO: 사장님 ID 가져와야함
		storeService.updateStoreReservationPolicyStatus(request.toCommand(ownerId, storeId));
		return ResponseEntity.ok()
			.body(ApiResponse.success("예약정책 상태 변경 성공", HttpStatus.OK));
	}
}
