package com.tablekok.waiting_server.application.client;

import java.util.UUID;

public interface StoreClient {

	// 음식점 ownerId 조회
	UUID getStoreOwner(UUID storeId);
}
