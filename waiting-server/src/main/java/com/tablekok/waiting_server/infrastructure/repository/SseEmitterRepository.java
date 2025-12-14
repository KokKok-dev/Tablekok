package com.tablekok.waiting_server.infrastructure.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {
	// Key: 고객 ID (UUID) 또는 Waiting ID, Value: SseEmitter 객체
	private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

	// 연결 저장
	public SseEmitter save(UUID waitingId, SseEmitter emitter) {
		emitters.put(waitingId, emitter);
		return emitter;
	}

	// 연결 삭제
	public void deleteById(UUID waitingId) {
		emitters.remove(waitingId);
	}

	// 특정 Emitter 조회
	public Optional<SseEmitter> findById(UUID waitingId) {
		return Optional.ofNullable(emitters.get(waitingId));
	}
}
