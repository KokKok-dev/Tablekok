package com.tablekok.hotreservationservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tablekok.dto.ApiResponse;
import com.tablekok.hotreservationservice.application.client.dto.GetStoreReservationPolicyResponse;

@FeignClient(name = "store-service", url = "${lb.dns.store}")
public interface StoreFeignClient {

	@GetMapping("/v1/stores/{storeId}/reservation-policy")
	ResponseEntity<ApiResponse<GetStoreReservationPolicyResponse>> getStoreReservationPolicy(
		@PathVariable UUID storeId);

	@GetMapping("/v1/internal/stores/popular")
	List<UUID> getPopularStores();

}
