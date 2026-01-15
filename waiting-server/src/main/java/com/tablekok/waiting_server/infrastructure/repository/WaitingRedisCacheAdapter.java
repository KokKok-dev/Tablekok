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

	@Override
	public int incrementAndGetLatestNumber(UUID storeId, int dbLastNumber) {
		String key = "store:" + storeId + ":latest_number";

		// 1. INCR 실행
		Long result = redisTemplate.opsForValue().increment(key);

		// 2. 만약 결과가 1이라면(키가 없었을 경우), DB 값으로 초기화
		if (result == 1) {
			// DB에서 가져온 값으로 다시 세팅 (DB값이 10이었다면 다음 번호는 11이 되어야 함)
			redisTemplate.opsForValue().set(key, String.valueOf(dbLastNumber + 1));
			return dbLastNumber + 1;
		}

		return result.intValue();
	}

	private String getQueueKey(UUID storeId) {
		return WAITING_KEY_PREFIX + storeId.toString();
	}
}
