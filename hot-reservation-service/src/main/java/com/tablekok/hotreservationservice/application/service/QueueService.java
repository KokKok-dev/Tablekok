package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
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

	@Value("${queue.sse.ttl}")
	private long SSE_TTL;

	@Value("${reservation.entry.ttl}")
	private long ENTRY_TTL;

	@Value("${reservation.available.user.limit}")
	private int availableUserLimit;

	// 사용자를 대기열에 등록하고 생성한 이미터를 반환
	public SseEmitter enterQueue(String userId) {
		SseEmitter newEmitter = new SseEmitter(SSE_TTL);
		this.emitters.put(userId, newEmitter);

		// 서버에서 emitter.complete()을 호출하거나, 클라이언트가 연결을 닫았을 때 실행될 콜백
		newEmitter.onCompletion(() -> {
				this.emitters.remove(userId);
				cacheStore.removeUserFromQueue(userId);
			}
		);
		newEmitter.onTimeout(newEmitter::complete);

		Double expireAt = cacheStore.findAvailableUser(userId);
		if (expireAt != null) {
			long remainingTime = (long)(expireAt - System.currentTimeMillis());
			sendEvent(newEmitter, userId, "entry", remainingTime);
			return newEmitter;
		}

		cacheStore.addUserToQueue(userId, SSE_TTL);
		Long rank = cacheStore.getRank(userId);
		sendEvent(newEmitter, userId, "queue", rank);

		return newEmitter;
	}

	// 예약 입장 유저인지 확인
	public void validateAvailableUser(String userId) {
		Double expireAt = cacheStore.findAvailableUser(userId);

		if (expireAt == null) {
			throw new AppException(HotReservationErrorCode.AVAILABLE_USER_VALIDATION_FAILED);
		}
	}

	// 예약 완료 또는 시간 초과 시 사용자, 이미터를 삭제합니다.
	public void completeReservation(String userId) {
		cacheStore.removeAvailableUser(userId);
		convertAndSend(userId, "done", "예약이 종료되었습니다.");

	}

	// 예약 허용 시간 초과 유저 삭제 및 입장 인원 반환
	public int processExpiredUsers() {
		long now = Instant.now().toEpochMilli();

		cacheStore.removeExpiredAvailableUsers(now);
		int availableUserCount = cacheStore.getAvailableUserCount();

		return availableUserLimit - availableUserCount;

	}

	// 유저 예약 입장 및 순번 갱신 알림
	public void processAllUsers(int count) {
		Set<String> allUsers = cacheStore.getAllUsers();
		int index = 0;
		for (String userId : allUsers) {
			if (index < count) {

				// 예약 가능자 처리
				addAvailableUser(userId);
				index++;
				continue;
			}

			// 나머지 사용자 순번 변경 알림. 몇명 입장 했는지 전달
			convertAndSend(userId, "update", String.valueOf(count));
			index++;
		}
	}

	// 예약 입장. AVAILABLE_USERS 리스트에 추가하고 알림
	private void addAvailableUser(String userId) {
		// 예약 가능 상태인 사용자 목록에 추가, 대기열에서 삭제
		cacheStore.addAvailableUser(userId, ENTRY_TTL);
		cacheStore.removeUserFromQueue(userId);

		convertAndSend(userId, "entry", String.valueOf(ENTRY_TTL));
	}

	public void onMessage(String message) {
		try {
			String[] parts = message.split(":");
			if (parts.length < 3)
				return;

			String userId = parts[0];
			String eventName = parts[1];
			String data = parts[2];

			SseEmitter emitter = emitters.get(userId);

			if (emitter != null) {
				log.info("내 서버에 연결된 유저 {}에게 {} 이벤트 전송", userId, eventName);
				sendEvent(emitter, userId, eventName, data);

				if ("done".equals(eventName)) {
					emitter.complete();
					emitters.remove(userId);
				}
			}

		} catch (Exception e) {
			log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage());
		}
	}

	private void convertAndSend(String userId, String eventName, String data) {
		String message = userId + ":" + eventName + ":" + data;
		cacheStore.convertAndSend(message);
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
