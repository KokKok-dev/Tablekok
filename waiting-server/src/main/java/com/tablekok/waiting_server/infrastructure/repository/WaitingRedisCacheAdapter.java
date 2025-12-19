package com.tablekok.waiting_server.infrastructure.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.tablekok.waiting_server.domain.repository.WaitingCachePort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WaitingRedisCacheAdapter implements WaitingCachePort {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String WAITING_KEY_PREFIX = "waiting:queue:";

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
	public void removeWaiting(UUID storeId, String waitingIdString) {
		String key = getQueueKey(storeId);
		redisTemplate.opsForZSet().remove(key, waitingIdString);
	}

	@Override
	public List<String> getWaitingIds(UUID storeId) {
		String key = getQueueKey(storeId);
		ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

		// 모든 멤버 조회
		// score가 낮은 순서로 정렬
		Set<String> members = zSetOps.range(key, 0, -1);

		if (members.isEmpty()) {
			return Collections.emptyList();
		}

		return new ArrayList<>(members);
	}

	private String getQueueKey(UUID storeId) {
		return WAITING_KEY_PREFIX + storeId.toString();
	}
}
