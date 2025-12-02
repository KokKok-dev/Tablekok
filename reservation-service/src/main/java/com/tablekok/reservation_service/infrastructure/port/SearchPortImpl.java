package com.tablekok.reservation_service.infrastructure.port;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.reservation_service.application.port.SearchPort;
import com.tablekok.reservation_service.application.port.dto.response.GetReservationPolicyResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchPortImpl implements SearchPort {
	private final SearchClient searchClient; //TODO 서치클라이언트 호출

	@Override
	public GetReservationPolicyResponse getReservationPolicy(UUID storeId) {
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
