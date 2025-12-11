package com.tablekok.hotreservationservice.application.service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.hotreservationservice.application.exception.HotReservationErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final CacheStore cacheStore;

	// 사용자를 대기열에 등록하고 현재 순위를 반환합니다.
	public Long enterQueue(String userId) {
		long score = Instant.now().toEpochMilli();
		cacheStore.addUserToQueue(userId, score);
		return cacheStore.getRank(userId);
	}

	// 대기열 순위를 조회합니다.
	public Long getRank(String userId) {
		return cacheStore.getRank(userId);
	}

	// 현재 대기열 모든 사용자 조회
	public Set<String> getAllUsers() {
		return cacheStore.getAllUsers();
	}

	// 예약 확정 토큰을 발급하고, AVAILABLE_USERS 리스트에 추가합니다.
	public String issueTokenAndRegister(String userId) {
		String token = UUID.randomUUID().toString();

		// 토큰 자체를 String으로 저장 (인증용)
		cacheStore.saveToken(userId, token);
		// 예약 가능 상태인 사용자 목록에 추가 (만료 시각은 현재 시각 + 토큰 유효 시간)
		long expirationTime = Instant.now().toEpochMilli();
		cacheStore.addAvailableUser(userId, expirationTime);

		return token;
	}

	// 대기열에서 사용자를 제거합니다.
	public void removeUserFromQueue(String userId) {
		cacheStore.removeUserFromQueue(userId);
	}

	// 사용자의 토큰 조회
	public String getToken(String userId) {
		return cacheStore.getToken(userId);
	}

	// 유효한 예약 토큰인지 확인합니다.
	public void validateToken(String userId, String token) {
		String storedToken = cacheStore.getToken(userId);

		if (!(storedToken != null && storedToken.equals(token))) {
			throw new AppException(HotReservationErrorCode.RESERVATION_TOKEN_VALIDATION_FAILED);
		}
	}

	// 예약 완료 또는 시간 초과로 사용된 토큰 및 상태를 삭제합니다.
	public void completeReservation(String userId) {
		cacheStore.removeToken(userId);
		cacheStore.removeAvailableUser(userId);
	}

	// 현재 예약 가능 상태인 사용자 목록을 반환합니다.
	public Map<Object, Object> getAvailableUsers() {
		return cacheStore.getAvailableUsers();
	}
}
