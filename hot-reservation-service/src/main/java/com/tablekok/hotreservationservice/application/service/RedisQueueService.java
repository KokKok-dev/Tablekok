package com.tablekok.hotreservationservice.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.hotreservationservice.application.exception.HotReservationErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisQueueService {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.queue.key}")
	private String QUEUE_KEY;
	@Value("${redis.token.prefix}")
	private String TOKEN_PREFIX;
	@Value("${redis.available.users.key}")
	private String AVAILABLE_USERS_KEY;
	@Value("${reservation.token.ttl}")
	private long tokenTtl;

	// 사용자를 대기열에 등록하고 현재 순위를 반환합니다.
	public Long enterQueue(String userId) {
		long score = Instant.now().toEpochMilli();
		redisTemplate.opsForZSet().add(QUEUE_KEY, userId, score);
		return redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
	}

	// 대기열 순위를 조회합니다.
	public Long getRank(String userId) {
		return redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
	}

	// 현재 대기열 모든 사용자 조회
	public Set<String> getAllUsers() {
		// start: 0, end: -1 -> 전체 멤버 조회
		return redisTemplate.opsForZSet().range(QUEUE_KEY, 0, -1);
	}

	// 예약 확정 토큰을 발급하고, AVAILABLE_USERS 리스트에 추가합니다.
	// @return 발급된 토큰 값
	public String issueTokenAndRegister(String userId) {
		String token = UUID.randomUUID().toString();

		// 1. 토큰 자체를 String으로 저장 (인증용)
		redisTemplate.opsForValue().set(TOKEN_PREFIX + userId, token, tokenTtl, TimeUnit.MILLISECONDS);

		// 2. 예약 가능 상태인 사용자 목록에 추가 (만료 시각은 현재 시각 + 토큰 유효 시간)
		long expirationTime = Instant.now().toEpochMilli() + tokenTtl;
		redisTemplate.opsForHash().put(AVAILABLE_USERS_KEY, userId, String.valueOf(expirationTime));

		return token;
	}

	// 대기열에서 사용자를 제거합니다.
	public void removeFromQueue(String userId) {
		redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
	}

	// 사용자의 토큰 조회
	public String getToken(String userId) {
		return redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);
	}

	// 유효한 예약 토큰인지 확인합니다.
	public void validateToken(String userId, String token) {
		String storedToken = redisTemplate.opsForValue().get(TOKEN_PREFIX + userId);

		if (!(storedToken != null && storedToken.equals(token))) {
			throw new AppException(HotReservationErrorCode.RESERVATION_TOKEN_VALIDATION_FAILED);
		}
	}

	// 예약 완료 또는 시간 초과로 사용된 토큰 및 상태를 삭제합니다.
	public void completeReservation(String userId) {
		redisTemplate.delete(TOKEN_PREFIX + userId);
		redisTemplate.opsForHash().delete(AVAILABLE_USERS_KEY, userId);
	}

	// 현재 예약 가능 상태인 사용자 목록을 반환합니다.
	public Map<Object, Object> getAvailableUsers() {
		return redisTemplate.opsForHash().entries(AVAILABLE_USERS_KEY);
	}
}
