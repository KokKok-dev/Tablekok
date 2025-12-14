package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.exception.AppException;
import com.tablekok.hotreservationservice.application.exception.HotReservationErrorCode;
import com.tablekok.hotreservationservice.domain.repository.CacheStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

	// 사용자 ID를 키로, SseEmitter를 값으로 저장하여 관리
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final CacheStore cacheStore;

	@Value("${queue.sse.timeout}")
	private Long TIMEOUT;

	@Value("${reservation.available.user.limit}")
	private int availableUserLimit;

	// 사용자를 대기열에 등록하고 생성한 이미터를 반환
	public SseEmitter enterQueue(String userId) {
		long score = Instant.now().toEpochMilli();

		SseEmitter newEmitter = new SseEmitter(TIMEOUT);
		this.emitters.put(userId, newEmitter);

		// 서버에서 emitter.complete()을 호출하거나, 클라이언트가 연결을 닫았을 때 실행될 콜백
		newEmitter.onCompletion(() -> {
				this.emitters.remove(userId);
				cacheStore.removeUserFromQueue(userId);
			}
		);
		newEmitter.onTimeout(newEmitter::complete);

		if (cacheStore.getRank(userId) != null) {
			cacheStore.removeUserFromQueue(userId);
		}

		cacheStore.addUserToQueue(userId, score);
		Long rank = cacheStore.getRank(userId);
		sendEvent(newEmitter, userId, "connect-rank", rank);

		return newEmitter;
	}

	// 유효한 예약 토큰인지 확인합니다.
	public void validateToken(String userId, String token) {
		String storedToken = cacheStore.getToken(userId);

		if (!(storedToken != null && storedToken.equals(token))) {
			throw new AppException(HotReservationErrorCode.RESERVATION_TOKEN_VALIDATION_FAILED);
		}
	}

	// 예약 완료 또는 시간 초과 시 사용자, 토큰, 이미터를 삭제합니다.
	public void completeReservation(String userId) {
		cacheStore.removeToken(userId);
		cacheStore.removeAvailableUser(userId);

		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			emitter.complete();
			emitters.remove(userId);
		}
	}

	// 예약 허용 시간 초과 유저 삭제 및 입장 인원 반환
	public int processExpiredUsers() {
		long now = Instant.now().toEpochMilli();

		Map<Object, Object> availableUsers = cacheStore.getAvailableUsers();

		availableUsers.forEach((userIdObj, expirationTimeObj) -> {
			String userId = userIdObj.toString();
			long expirationTime = Long.parseLong(expirationTimeObj.toString());

			// 만료 시간 초과 시
			if (now >= expirationTime) {
				// Redis에서 상태 삭제 (다음 대기열 사람을 받을 수 있게 됨)
				completeReservation(userId);
			}
		});

		return availableUserLimit - availableUsers.size();

	}

	// 유저 예약 입장 및 순번 갱신 알림
	public void processAllUsers(int count) {
		Set<String> allUsers = cacheStore.getAllUsers();
		int index = 0;
		for (String userId : allUsers) {
			if (index < count) {

				// 예약 가능자 처리
				issueTokenAndRegister(userId);
				index++;
				continue;
			}

			// 나머지 사용자 순번 변경 알림. 몇명 입장 했는지 전달
			SseEmitter emitter = emitters.get(userId);
			sendEvent(emitter, userId, "update", count);
			index++;
		}
	}

	// 예약 확정 토큰을 발급하고, AVAILABLE_USERS 리스트에 추가합니다.
	private void issueTokenAndRegister(String userId) {
		String token = UUID.randomUUID().toString();

		// 토큰 자체를 String으로 저장 (인증용)
		cacheStore.saveToken(userId, token);

		// 예약 가능 상태인 사용자 목록에 추가 (만료 시각은 현재 시각 + 토큰 유효 시간)
		long expirationTime = Instant.now().toEpochMilli();
		cacheStore.addAvailableUser(userId, expirationTime);

		cacheStore.removeUserFromQueue(userId);

		SseEmitter emitter = emitters.get(userId);
		sendEvent(emitter, userId, "issue-token", token);

	}

	private <T> void sendEvent(SseEmitter emitter, String userId, String eventName, T data) {
		try {
			emitter.send(SseEmitter.event()
				.name(eventName)
				.data(data)
				.id(userId)
			);
		} catch (IOException e) {
			emitter.completeWithError(e);
			emitters.remove(userId);
			log.warn(e.getMessage());
		}

	}

}
