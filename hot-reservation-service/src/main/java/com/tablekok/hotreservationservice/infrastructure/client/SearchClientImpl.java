package com.tablekok.hotreservationservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.application.client.SearchClient;
import com.tablekok.hotreservationservice.application.client.dto.GetReservationPolicyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchClientImpl implements SearchClient {
	private final SearchFeignClient searchFeignClient; //TODO 서치클라이언트 호출

	@Override
	public GetReservationPolicyResponse getStoreReservationPolicy(UUID storeId) {
		return null;
	}

	@Override
	public List<UUID> getHotStores() {
		return List.of();
	}

}
