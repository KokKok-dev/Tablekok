package com.tablekok.store_service.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tablekok.store_service.application.service.InternalStoreService;
import com.tablekok.store_service.presentation.dto.request.OwnerVerificationRequest;
import com.tablekok.store_service.presentation.dto.response.StoreOwnerResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/internal/stores")
@AllArgsConstructor
public class InternalStoreController {

	private final InternalStoreService internalStoreService;

	@GetMapping("/popular")
	public ResponseEntity<List<UUID>> getPopularRestaurants() {

		List<UUID> popularIds = internalStoreService.findPopularStores();
		return ResponseEntity.ok(popularIds);
	}

	@PostMapping("/verify-owner")
	public ResponseEntity<Boolean> verifyStoreOwner(
		@RequestBody OwnerVerificationRequest request
	) {
		boolean isOwner = internalStoreService.isOwner(request.storeId(), request.ownerId());
		return ResponseEntity.ok(isOwner);
	}

	@GetMapping("/{storeId}/owner")
	public ResponseEntity<StoreOwnerResponse> getStoreOwner(
		@PathVariable UUID storeId
	) {
		UUID ownerId = internalStoreService.getOwnerIdByStoreId(storeId);
		return ResponseEntity.ok(new StoreOwnerResponse(storeId, ownerId));
	}
}
