package com.tablekok.waiting_server.presentation.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.dto.ApiResponse;
import com.tablekok.dto.auth.AuthUser;
import com.tablekok.waiting_server.application.dto.result.GetWaitingQueueResult;
import com.tablekok.waiting_server.application.service.WaitingOwnerService;
import com.tablekok.waiting_server.presentation.dto.request.StartWaitingServiceRequest;
import com.tablekok.waiting_server.presentation.dto.response.GetWaitingQueueResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/stores/{storeId}/waiting")
@RequiredArgsConstructor
public class WaitingOwnerController {

	private final WaitingOwnerService waitingOwnerService;

	// 사장님이 해당 매장의 웨이팅 기능을 활성화합니다.
	@PostMapping("/start")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> startWaitingService(
		@PathVariable UUID storeId,
		@Valid @RequestBody StartWaitingServiceRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());
		waitingOwnerService.startWaitingService(request.toCommand(storeId, ownerId));
		return ResponseEntity.ok(
			ApiResponse.success("음식점 웨이팅 시작합니다.", HttpStatus.OK)
		);
	}

	@GetMapping("/connect")
	public SseEmitter connectNotification(
		@PathVariable UUID storeId) {
		return waitingOwnerService.connectOwnerWaitingNotification(storeId);
	}

	// 사장님이 해당 매장의 웨이팅 기능을 비활성화합니다.
	@PostMapping("/stop")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> stopWaitingService(
		@PathVariable UUID storeId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());

		waitingOwnerService.stopWaitingService(storeId, ownerId);
		return ResponseEntity.ok(
			ApiResponse.success("음식점 웨이팅 중단합니다.", HttpStatus.OK)
		);
	}

	// 웨이팅 큐(대기열) 조회 (Current Queue Status)
	@GetMapping("/queue")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<List<GetWaitingQueueResponse>>> getWaitingQueue(
		@PathVariable UUID storeId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());

		List<GetWaitingQueueResult> results = waitingOwnerService.getStoreWaitingQueue(storeId, ownerId);
		List<GetWaitingQueueResponse> responseList = results.stream()
			.map(GetWaitingQueueResponse::from)
			.collect(Collectors.toList());

		return ResponseEntity.ok(
			ApiResponse.success("음식점 웨이팅 큐 조회 성공", responseList, HttpStatus.OK)
		);
	}

	// 고객 호출 (Waiting Status -> CALLED)
	@PostMapping("/{waitingId}/call")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> callWaiting(
		@PathVariable UUID storeId,
		@PathVariable UUID waitingId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());

		waitingOwnerService.callWaiting(storeId, waitingId, ownerId);
		return ResponseEntity.ok(
			ApiResponse.success("고객 호출 성공 (CALLED 상태로 변경)", HttpStatus.OK)
		);
	}

	// 고객 입장 처리 (Waiting Status -> ENTERED)
	@PostMapping("/{waitingId}/enter")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> enterWaiting(
		@PathVariable UUID storeId,
		@PathVariable UUID waitingId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());

		waitingOwnerService.enterWaiting(storeId, waitingId, ownerId);
		return ResponseEntity.ok(
			ApiResponse.success("고객 입장 처리 성공 (ENTERED 상태로 변경)", HttpStatus.OK)
		);
	}

	// 사장님 취소 처리 (Waiting Status -> OWNER_CANCELED)
	@PostMapping("/{waitingId}/owner-cancel")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> cancelByOwner(
		@PathVariable UUID storeId,
		@PathVariable UUID waitingId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());
		waitingOwnerService.cancelByOwner(storeId, waitingId, ownerId);

		return ResponseEntity.ok(
			ApiResponse.success("매장 측 웨이팅 취소 성공 (OWNER_CANCELED 상태로 변경)", HttpStatus.OK)
		);
	}

	// 사장님 No-Show 수동 처리 (Waiting Status -> NO_SHOW)
	@PostMapping("/{waitingId}/no-show")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<Void>> markNoShow(
		@PathVariable UUID storeId,
		@PathVariable UUID waitingId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID ownerId = UUID.fromString(authUser.userId());
		waitingOwnerService.markNoShow(storeId, waitingId, ownerId);

		return ResponseEntity.ok(
			ApiResponse.success("고객 No-Show 수동 처리 성공 (NO_SHOW 상태로 변경)", HttpStatus.OK)
		);
	}
}
