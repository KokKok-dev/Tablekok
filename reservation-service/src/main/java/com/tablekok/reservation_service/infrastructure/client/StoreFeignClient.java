package com.tablekok.reservation_service.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tablekok.dto.ApiResponse;
import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;

@FeignClient(name = "store-service")
public interface StoreFeignClient {

	@GetMapping("/v1/stores/{storeId}/reservation-policy")
	ResponseEntity<ApiResponse<GetStoreReservationPolicyResponse>> getStoreReservationPolicy(
		@PathVariable UUID storeId);

	@PostMapping("/v1/internal/stores/verify-owner")
	ResponseEntity<Boolean> verifyStoreOwner(@RequestBody OwnerVerificationRequest request);

	@GetMapping("/v1/internal/stores/popular")
	ResponseEntity<List<UUID>> getPopularStores();
}
