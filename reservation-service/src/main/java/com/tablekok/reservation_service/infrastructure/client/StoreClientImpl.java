package com.tablekok.reservation_service.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.client.StoreClient;
import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;

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
			throw new AppException(ReservationErrorCode.INTERNAL_CANNOT_CONNECT);
		}
	}

	@Override
	public boolean checkStoreOwner(OwnerVerificationRequest request) {
		try {
			return storeFeignClient.verifyStoreOwner(request).getBody().booleanValue();
		} catch (Exception e) {
			throw new AppException(ReservationErrorCode.INTERNAL_CANNOT_CONNECT);
		}
	}

	@Override
	public List<UUID> getHotStores() {
		try {
			return storeFeignClient.getPopularStores().getBody();
		} catch (Exception e) {
			throw new AppException(ReservationErrorCode.INTERNAL_CANNOT_CONNECT);
		}
	}

}
