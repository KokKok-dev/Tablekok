package com.tablekok.waiting_server.presentation.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tablekok.dto.ApiResponse;
import com.tablekok.waiting_server.application.dto.result.GetWaitingQueueResult;
import com.tablekok.waiting_server.application.service.WaitingOwnerService;
import com.tablekok.waiting_server.presentation.dto.response.GetWaitingQueueResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/store/{storeId}/waiting")
@RequiredArgsConstructor
public class WaitingOwnerController {

	private final WaitingOwnerService waitingOwnerService;

	@GetMapping("/queue")
	public ResponseEntity<ApiResponse<List<GetWaitingQueueResponse>>> getWaitingQueue(
		@PathVariable UUID storeId
	) {
		List<GetWaitingQueueResult> results = waitingOwnerService.getStoreWaitingQueue(storeId);
		List<GetWaitingQueueResponse> responseList = results.stream()
			.map(GetWaitingQueueResponse::from)
			.collect(Collectors.toList());

		return ResponseEntity.ok(
			ApiResponse.success("음식점 웨이팅 큐 조회 성공", responseList, HttpStatus.OK)
		);
	}
}
