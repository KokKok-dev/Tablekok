package com.tablekok.waiting_server.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.waiting_server.application.dto.result.CreateWaitingResult;
import com.tablekok.waiting_server.application.service.WaitingService;
import com.tablekok.waiting_server.presentation.dto.request.CreateWaitingRequest;
import com.tablekok.waiting_server.presentation.dto.response.CreateWaitingResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/waiting")
@RequiredArgsConstructor
public class WaitingUserController {

	private final WaitingService waitingService;

	@PostMapping("/{storeId}")
	public ResponseEntity<ApiResponse<CreateWaitingResponse>> createWaiting(
		@PathVariable UUID storeId,
		@Valid @RequestBody CreateWaitingRequest request
	) {
		// TODO: 회원인지 비회원인지 판별 후 Command 전달

		CreateWaitingResult result = waitingService.createWaiting(request.toCommand(storeId));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{waitingId}")
			.buildAndExpand(result.waitingId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("음식점 생성 성공", CreateWaitingResponse.from(result), HttpStatus.CREATED));
	}

}
