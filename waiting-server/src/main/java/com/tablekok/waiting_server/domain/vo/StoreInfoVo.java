package com.tablekok.waiting_server.domain.vo;

import java.time.LocalTime;
import java.util.UUID;

public record StoreInfoVo(
	UUID storeId,
	UUID ownerId,
	String storeName,
	LocalTime openTime,
	LocalTime closeTime
) {
}
