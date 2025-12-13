package com.tablekok.hotreservationservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.application.client.StoreClient;
import com.tablekok.hotreservationservice.application.client.dto.GetStoreReservationPolicyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreClientImpl implements StoreClient {
	private final StoreFeignClient storeFeignClient; //TODO 서치클라이언트 호출

	@Override
	public GetStoreReservationPolicyResponse getStoreReservationPolicy(UUID storeId) {
		return storeFeignClient.getStoreReservationPolicy(storeId).getBody().getData();
	}

	@Override
	public List<UUID> getHotStores() {
		return storeFeignClient.getPopularStores();
	}

}
