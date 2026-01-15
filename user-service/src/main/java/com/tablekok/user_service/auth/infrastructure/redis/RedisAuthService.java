package com.tablekok.user_service.auth.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisAuthService {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${redis.auth.refresh-prefix}")
	private String refreshPrefix;

	@Value("${redis.auth.invalid-before-prefix}")
	private String invalidBeforePrefix;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	// Refresh Token 저장
	public void saveRefreshToken(UUID userId, String refreshToken) {
		String key = refreshPrefix + userId.toString();
		redisTemplate.opsForValue().set(
			key,
			refreshToken,
			Duration.ofMillis(refreshTokenExpiration)
		);
	}

	// Refresh Token 조회
	public String getRefreshToken(UUID userId) {
		String key = refreshPrefix + userId.toString();
		return redisTemplate.opsForValue().get(key);
	}

	// Refresh Token 삭제
	public void deleteRefreshToken(UUID userId) {
		String key = refreshPrefix + userId.toString();
		redisTemplate.delete(key);
	}

	// 토큰 무효화 시점 저장 (비밀번호 변경 시)
	public void saveTokenInvalidBefore(UUID userId) {
		String key = invalidBeforePrefix + userId.toString();
		redisTemplate.opsForValue().set(
			key,
			Instant.now().toString(),
			Duration.ofMinutes(30)
		);
	}

	// 토큰 무효화 시점 조회
	public Instant getTokenInvalidBefore(UUID userId) {
		String key = invalidBeforePrefix + userId.toString();
		String value = redisTemplate.opsForValue().get(key);
		return value != null ? Instant.parse(value) : null;
	}
}
