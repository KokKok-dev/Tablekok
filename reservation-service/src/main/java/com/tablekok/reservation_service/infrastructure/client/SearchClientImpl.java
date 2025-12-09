package com.tablekok.reservation_service.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.reservation_service.application.client.SearchClient;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchClientImpl implements SearchClient {
	private final SearchFeignClient searchClient; //TODO 서치클라이언트 호출

	@Override
	public GetStoreReservationPolicyResponse getReservationPolicy(UUID storeId) {
		return null;
	}

	@Override
	public boolean checkStoreOwner(UUID userId, UUID storeId) {
		return true;
	}

	@Override
	public List<UUID> getHotStores() {
		return List.of();
	}

}
