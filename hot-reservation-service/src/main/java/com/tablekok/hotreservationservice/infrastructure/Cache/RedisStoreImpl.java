package com.tablekok.hotreservationservice.infrastructure.Cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.application.service.CacheStore;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisStoreImpl implements CacheStore {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.queue.key}")
	private String QUEUE_KEY;
	@Value("${redis.token.prefix}")
	private String TOKEN_PREFIX;
	@Value("${redis.available.users.key}")
	private String AVAILABLE_USERS_KEY;
	@Value("${reservation.token.ttl}")
	private long tokenTtl;

	@Override
	public void addUserToQueue(String userId, long score) {
		redisTemplate.opsForZSet().add(QUEUE_KEY, userId, score);
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
	public void saveToken(String userId, String token) {
		redisTemplate.opsForValue().set(TOKEN_PREFIX + userId, token, tokenTtl, TimeUnit.MILLISECONDS);
	}

	@Override
	public void addAvailableUser(String userId, long expirationTime) {
		redisTemplate.opsForHash().put(AVAILABLE_USERS_KEY, userId, String.valueOf(expirationTime + tokenTtl));
	}

	@Override
	public void removeUserFromQueue(String userId) {
		redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
	}

	@Override
	public String getToken(String userId) {
		return redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
	}

	@Override
	public void removeToken(String userId) {
		redisTemplate.delete(TOKEN_PREFIX + userId);
	}

	@Override
	public void removeAvailableUser(String userId) {
		redisTemplate.opsForHash().delete(AVAILABLE_USERS_KEY, userId);
	}

	@Override
	public Map<Object, Object> getAvailableUsers() {
		return redisTemplate.opsForHash().entries(AVAILABLE_USERS_KEY);
	}
}
