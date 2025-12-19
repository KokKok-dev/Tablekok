package com.tablekok.hotreservationservice.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.hotreservationservice.application.client.StoreClient;
import com.tablekok.hotreservationservice.application.client.dto.GetStoreReservationPolicyResponse;
import com.tablekok.hotreservationservice.application.exception.HotReservationErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreClientImpl implements StoreClient {
	private final StoreFeignClient storeFeignClient;

	@Override
	public GetStoreReservationPolicyResponse getStoreReservationPolicy(UUID storeId) {
		try {
			return storeFeignClient.getStoreReservationPolicy(storeId).getBody().getData();
		} catch (Exception e) {
			throw new AppException(HotReservationErrorCode.INTERNAL_CANNOT_CONNECT);
		}
	}

	@Override
	public List<UUID> getHotStores() {
		try {
			return storeFeignClient.getPopularStores();
		} catch (Exception e) {
			throw new AppException(HotReservationErrorCode.INTERNAL_CANNOT_CONNECT);
		}
	}

}
