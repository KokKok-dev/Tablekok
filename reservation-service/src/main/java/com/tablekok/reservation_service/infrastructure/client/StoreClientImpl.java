package com.tablekok.reservation_service.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.reservation_service.application.client.StoreClient;
import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreClientImpl implements StoreClient {
	private final StoreFeignClient storeFeignClient;

	@Override
	public GetStoreReservationPolicyResponse getStoreReservationPolicy(UUID storeId) {
		return storeFeignClient.getStoreReservationPolicy(storeId).getBody().getData();
	}

	@Override
	public boolean checkStoreOwner(OwnerVerificationRequest request) {
		return storeFeignClient.verifyStoreOwner(request).getBody().booleanValue();
	}

	@Override
	public List<UUID> getHotStores() {
		return storeFeignClient.getPopularStores().getBody();
	}

}
