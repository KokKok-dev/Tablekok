package com.tablekok.waiting_server.domain.repository;

import java.util.UUID;

public interface WaitingCachePort {

	// ZSET에 웨이팅 항목을 추가합니다. (Score: 웨이팅번호, Member: 사용자 식별자)
	void addWaiting(UUID storeId, int waitingNumber, String memberKey);

	// ZSET에서 특정 항목의 대기 순위(Rank, 1-based)를 조회합니다.
	Long getRank(UUID storeId, String memberKey);

	Long getCardinality(UUID storeId);
}
