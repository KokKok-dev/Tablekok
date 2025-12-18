package com.tablekok.waiting_server.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tablekok.waiting_server.application.client.StoreClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreClientImpl implements StoreClient {

	private final StoreFeignClient storeFeignClient;

	@Override
	public UUID getStoreOwner(UUID storeId) {
		return storeFeignClient.getStoreOwner(storeId).getBody().ownerId();
	}
}
