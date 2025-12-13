package com.tablekok.waiting_server.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.waiting_server.application.dto.result.CreateWaitingResult;
import com.tablekok.waiting_server.application.dto.result.GetWaitingResult;
import com.tablekok.waiting_server.application.service.WaitingUserService;
import com.tablekok.waiting_server.presentation.dto.request.CreateWaitingRequest;
import com.tablekok.waiting_server.presentation.dto.response.CreateWaitingResponse;
import com.tablekok.waiting_server.presentation.dto.response.GetWaitingResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/waiting")
@RequiredArgsConstructor
public class WaitingUserController {

	private final WaitingUserService waitingService;

	@PostMapping("/{storeId}")
	public ResponseEntity<ApiResponse<CreateWaitingResponse>> createWaiting(
		@PathVariable UUID storeId,
		@Valid @RequestBody CreateWaitingRequest request
	) {
		UUID memberId = UUID.randomUUID();

		// TODO: 로그인 사용자면 memberId 넘겨주고 아니면 null return
		CreateWaitingResult result = waitingService.createWaiting(request.toCommand(storeId, memberId));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{waitingId}")
			.buildAndExpand(result.waitingId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("웨이팅 생성 성공", CreateWaitingResponse.from(result), HttpStatus.CREATED));
	}

	@GetMapping("/{waitingId}")
	public ResponseEntity<ApiResponse<GetWaitingResponse>> getWaiting(
		@PathVariable UUID waitingId
	) {
		GetWaitingResult result = waitingService.getWaiting(waitingId);
		return ResponseEntity.ok(
			ApiResponse.success("웨이팅 정보 조회 성공", GetWaitingResponse.from(result), HttpStatus.OK)
		);
	}

	@PostMapping("/{waitingId}/confirm")
	public ResponseEntity<ApiResponse<Void>> confirmWaiting(
		@PathVariable UUID waitingId
	) {
		waitingService.confirmWaiting(waitingId);
		return ResponseEntity.ok(
			ApiResponse.success("웨이팅 confirm 상태 변경 성공", HttpStatus.OK)
		);
	}

	@PostMapping("/{waitingId}/cancel")
	public ResponseEntity<ApiResponse<Void>> cancelWaiting(
		@PathVariable UUID waitingId
	) {
		waitingService.cancelWaiting(waitingId);
		return ResponseEntity.ok(
			ApiResponse.success("웨이팅 cancel 상태 변경 성공", HttpStatus.OK)
		);
	}

}
