package com.tablekok.hotreservationservice.application.client;

import java.util.List;
import java.util.UUID;

import com.tablekok.hotreservationservice.application.client.dto.GetReservationPolicyResponse;

public interface SearchClient {
	// 해당 음식점의 정책 조회
	GetReservationPolicyResponse getStoreReservationPolicy(UUID storeId);

	// 인기 음식점 리스트 조회
	List<UUID> getHotStores();
}
