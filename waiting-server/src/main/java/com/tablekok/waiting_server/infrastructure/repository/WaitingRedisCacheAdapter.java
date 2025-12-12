package com.tablekok.waiting_server.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.tablekok.waiting_server.domain.repository.WaitingCachePort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WaitingRedisCacheAdapter implements WaitingCachePort {

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void addWaiting(UUID storeId, int waitingNumber, String memberKey) {
		String key = getQueueKey(storeId);
		// Score는 웨이팅 번호 (낮은 번호가 높은 우선순위를 갖도록 오름차순 정렬)
		Double score = (double)waitingNumber;
		redisTemplate.opsForZSet().add(key, memberKey, score);
	}

	@Override
	public Long getRank(UUID storeId, String memberKey) {
		String key = getQueueKey(storeId);
		return redisTemplate.opsForZSet().rank(key, memberKey);
	}

	@Override
	public Long getCardinality(UUID storeId) {
		String key = getQueueKey(storeId);
		return redisTemplate.opsForZSet().size(key);
	}

	@Override
	public void removeWaiting(UUID storeId, String waitingIdString) {
		String key = getQueueKey(storeId);
		redisTemplate.opsForZSet().remove(key, waitingIdString);
	}

	private String getQueueKey(UUID storeId) {
		return "waiting:store:" + storeId.toString();
	}
}
