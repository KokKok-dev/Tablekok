package com.tablekok.waiting_server.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tablekok.waiting_server.infrastructure.client.dto.StoreWaitingInternalResponse;

@FeignClient(name = "store-service", url = "${lb.dns.store}")
public interface StoreFeignClient {
	@GetMapping("/v1/internal/stores/{storeId}/waiting-details")
	ResponseEntity<StoreWaitingInternalResponse> getStoreDetails(@PathVariable UUID storeId);

}
