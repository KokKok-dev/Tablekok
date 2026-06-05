package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.exception.AppException;
import com.tablekok.hotreservationservice.application.dto.QueueMessage;
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
	private final ObjectMapper objectMapper;

	@Value("${queue.sse.ttl}")
	private long SSE_TTL;

	@Value("${reservation.entry.ttl}")
	private long ENTRY_TTL;

	@Value("${reservation.available.user.limit}")
	private int availableUserLimit;

	// 사용자를 대기열에 등록하고 생성한 이미터를 반환
	public SseEmitter enterQueue(String userId) {
		SseEmitter newEmitter = new SseEmitter(SSE_TTL);
		// 같은 유저의 이전 연결이 있으면(중복/빠른 재연결) 옛 연결을 즉시 정리
		SseEmitter oldEmitter = this.emitters.put(userId, newEmitter);
		if (oldEmitter != null) {
			oldEmitter.complete();
		}

		// 서버에서 emitter.complete()을 호출하거나, 클라이언트가 연결을 닫았을 때 실행될 콜백
		newEmitter.onCompletion(() -> {
				// 현재 매핑이 '이 emitter'일 때만 정리 (재연결로 교체됐으면 건드리지 않음)
				if (this.emitters.remove(userId, newEmitter)) {
					cacheStore.removeUserFromQueue(userId);
				}
			}
		);
		newEmitter.onTimeout(newEmitter::complete);

		Double expireAt = cacheStore.findAvailableUser(userId);
		if (expireAt != null) {
			long remainingTime = (long)(expireAt - System.currentTimeMillis());
			sendEvent(newEmitter, userId, "entry", remainingTime);
			return newEmitter;
		}

		// 큐에 없을 때만 추가 → 재연결해도 순번 유지
		cacheStore.addUserToQueueIfAbsent(userId, SSE_TTL);
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
		publish(QueueMessage.done(userId));
	}

	// 예약 허용 시간 초과 유저 삭제 및 입장 인원 반환
	public int processExpiredUsers() {
		long now = Instant.now().toEpochMilli();

		cacheStore.removeExpiredAvailableUsers(now);
		int availableUserCount = cacheStore.getAvailableUserCount();

		return availableUserLimit - availableUserCount;

	}

	// 유저 예약 입장 및 순번 갱신 알림 — 입장 그룹만 모아 한 번에 발행
	public void processAllUsers(int count) {
		// 입장 대상 상위 count 명만 조회
		List<String> entryUserIds = new ArrayList<>(cacheStore.getTopUsers(count));
		if (entryUserIds.isEmpty()) {   // ← 빈자리는 있지만 큐가 비어서 입장자 0인 경우
			return;
		}
		
		// 예약 가능 공간으로 일괄 이동 + 대기열에서 일괄 삭제 (각각 단일 명령)
		cacheStore.addAvailableUsers(entryUserIds, ENTRY_TTL);
		cacheStore.removeUsersFromQueue(entryUserIds);

		// 한 번의 발행: 입장 리스트 + 입장 TTL + 대기 갱신 인원(count)
		publish(QueueMessage.tick(entryUserIds, String.valueOf(ENTRY_TTL), String.valueOf(count)));
	}

	public void onMessage(String raw) {
		try {
			QueueMessage message = objectMapper.readValue(raw, QueueMessage.class);
			switch (message.type()) {
				case TICK -> handleTick(message);
				case DONE -> handleDone(message);
			}
		} catch (Exception e) {
			log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage());
		}
	}

	// 입장 유저에게 entry 전송 + 이 서버의 모든 emitter 에 순번 갱신 브로드캐스트
	private void handleTick(QueueMessage message) {
		if (message.entryUserIds() != null) {
			for (String userId : message.entryUserIds()) {
				SseEmitter emitter = emitters.get(userId);
				if (emitter != null) {
					log.info("내 서버에 연결된 입장 유저 {}에게 entry 전송", userId);
					sendEvent(emitter, userId, "entry", message.entryTtl());
				}
			}
		}

		emitters.forEach((userId, emitter) -> sendEvent(emitter, userId, "update", message.updateCount()));
	}

	// 예약 종료 유저에게 done 전송 후 emitter 정리
	private void handleDone(QueueMessage message) {
		String userId = message.doneUserId();
		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			log.info("내 서버에 연결된 유저 {}에게 done 이벤트 전송", userId);
			sendEvent(emitter, userId, "done", "예약이 종료되었습니다.");
			emitter.complete();
			emitters.remove(userId, emitter);
		}
	}

	private void publish(QueueMessage message) {
		try {
			cacheStore.convertAndSend(objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			log.error("pub/sub 메시지 직렬화 실패: {}", e.getMessage());
		}
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
			emitters.remove(userId, emitter);
			log.warn(e.getMessage());
		}

	}

}
