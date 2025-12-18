package com.tablekok.waiting_server.application.client;

import java.util.UUID;

import com.tablekok.waiting_server.domain.vo.StoreInfoVo;

public interface StoreClient {

	// 음식점 ownerId 조회
	StoreInfoVo getStoreDetails(UUID storeId);
}
