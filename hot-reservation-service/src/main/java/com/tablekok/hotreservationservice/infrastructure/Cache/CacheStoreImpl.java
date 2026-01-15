package com.tablekok.hotreservationservice.infrastructure.Cache;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.domain.repository.CacheStore;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheStoreImpl implements CacheStore {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.queue.key}")
	private String QUEUE_KEY;
	@Value("${redis.available.users.key}")
	private String AVAILABLE_USERS_KEY;
	@Value("${redis.pubsub.channel}")
	private String PUB_SUB_CHANNEL;

	@Override
	public void addUserToQueue(String userId, long sseTtl) {
		long expireAt = System.currentTimeMillis() + sseTtl;
		redisTemplate.opsForZSet().add(QUEUE_KEY, userId, (double)expireAt);
	}

	@Override
	public Long getRank(String userId) {
		return redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
	}

	@Override
	public Set<String> getAllUsers() {
		// start: 0, end: -1 -> 전체 멤버 조회
		return redisTemplate.opsForZSet().range(QUEUE_KEY, 0, -1);
	}

	@Override
	public void addAvailableUser(String userId, long entryTtl) {
		long expireAt = System.currentTimeMillis() + entryTtl;
		redisTemplate.opsForZSet().add(AVAILABLE_USERS_KEY, userId, (double)expireAt);
	}

	@Override
	public void removeUserFromQueue(String userId) {
		redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
	}

	@Override
	public Double findAvailableUser(String userId) {
		return redisTemplate.opsForZSet().score(AVAILABLE_USERS_KEY, userId);
	}

	@Override
	public void removeAvailableUser(String userId) {
		redisTemplate.opsForZSet().remove(AVAILABLE_USERS_KEY, userId);
	}

	@Override
	public void removeExpiredAvailableUsers(long now) {
		redisTemplate.opsForZSet().removeRangeByScore(AVAILABLE_USERS_KEY, 0, (double)now);
	}

	@Override
	public int getAvailableUserCount() {
		return redisTemplate.opsForZSet().zCard(AVAILABLE_USERS_KEY).intValue();
	}

	@Override
	public void convertAndSend(String message) {
		redisTemplate.convertAndSend(PUB_SUB_CHANNEL, message);
	}
}
