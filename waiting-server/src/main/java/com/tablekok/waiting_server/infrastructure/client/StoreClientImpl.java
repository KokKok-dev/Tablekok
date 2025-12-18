package com.tablekok.waiting_server.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.client.StoreClient;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.domain.vo.StoreInfoVo;
import com.tablekok.waiting_server.infrastructure.client.dto.StoreWaitingInternalResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreClientImpl implements StoreClient {

	private final StoreFeignClient storeFeignClient;

	@Override
	public StoreInfoVo getStoreDetails(UUID storeId) {
		StoreWaitingInternalResponse response = storeFeignClient.getStoreDetails(storeId).getBody();

		if (response == null) {
			throw new AppException(WaitingErrorCode.STORE_NOT_FOUND);
		}

		return new StoreInfoVo(
			response.storeId(),
			response.ownerId(),
			response.storeName(),
			response.openTime(),
			response.closeTime()
		);
	}
}
