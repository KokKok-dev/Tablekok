package com.tablekok.reservation_service.application.client;

import java.util.List;
import java.util.UUID;

import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;

public interface StoreClient {
	// 해당 음식점의 정책 조회
	GetStoreReservationPolicyResponse getStoreReservationPolicy(UUID storeId);

	// 유저가 해당 음식점의 오너가 맞는지
	boolean checkStoreOwner(OwnerVerificationRequest request);

	// 인기 음식점 리스트 조회
	List<UUID> getHotStores();
}
