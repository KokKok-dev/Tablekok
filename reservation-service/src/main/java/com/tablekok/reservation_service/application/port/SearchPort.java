package com.tablekok.reservation_service.application.port;

import java.util.List;
import java.util.UUID;

import com.tablekok.reservation_service.application.port.dto.response.GetReservationPolicyResponse;

public interface SearchPort {
	// 해당 음식점의 정책 조회
	GetReservationPolicyResponse getReservationPolicy(UUID storeId);

	// 유저가 해당 음식점의 오너가 맞는지
	boolean checkStoreOwner(UUID userId, UUID storeId);

	// 인기 음식점 리스트 캐시에서 가져오거나 없으면 내부 호출
	List<UUID> getHotStores();
}
