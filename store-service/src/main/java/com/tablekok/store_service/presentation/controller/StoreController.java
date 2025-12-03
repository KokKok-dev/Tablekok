package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.store_service.presentation.dto.request.CreateStoreRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateStatusRequest;
import com.tablekok.store_service.presentation.dto.request.UpdateStoreRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/stores")
public class StoreController {

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createStore(
		@Valid @RequestBody CreateStoreRequest request
	) {
		// store 생성
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{storeId}")
			.buildAndExpand(UUID.randomUUID())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("음식점 생성 성공", HttpStatus.CREATED));
	}

	@PutMapping("/{storeId}")
	public ResponseEntity<ApiResponse<Void>> updateStore(
		@PathVariable UUID storeId,
		@RequestBody UpdateStoreRequest request
	) {
		// 음식점 정보 수정
		return ResponseEntity.ok(
			ApiResponse.success("음식점 정보 수정 성공", HttpStatus.OK)
		);
	}

	@PatchMapping("/{storeId}")
	public ResponseEntity<ApiResponse<Void>> updateStatus(
		@PathVariable UUID storeId,
		@RequestBody UpdateStatusRequest request
	) {
		// 음식점 상태 변경
		return ResponseEntity.ok(
			ApiResponse.success("음식점 상태 수정 성공", HttpStatus.OK)
		);
	}

	@DeleteMapping("/{storeId}")
	public ResponseEntity<ApiResponse<Void>> deleteStore(
		@PathVariable UUID storeId
	) {
		// 음식점 삭제
		return ResponseEntity.ok(
			ApiResponse.success("음식점 삭제 성공", HttpStatus.OK)
		);
	}

}
