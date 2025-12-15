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
	private final Map<UUID, SseEmitter> customerEmitters = new ConcurrentHashMap<>();
	private final Map<UUID, SseEmitter> ownerEmitters = new ConcurrentHashMap<>();

	// ============ [고객 Emitter 관리 메서드] ============
	// 연결 저장
	public SseEmitter saveCustomerEmitter(UUID waitingId, SseEmitter emitter) {
		customerEmitters.put(waitingId, emitter);
		return emitter;
	}

	// 연결 삭제
	public void deleteCustomerEmitter(UUID waitingId) {
		customerEmitters.remove(waitingId);
	}

	// 특정 Emitter 조회
	public Optional<SseEmitter> findCustomerEmitter(UUID waitingId) {
		return Optional.ofNullable(customerEmitters.get(waitingId));
	}

	// ============ [사장님 Emitter 관리 메서드] ============
	// 사장님 연결 저장
	public SseEmitter saveOwnerEmitter(UUID storeId, SseEmitter emitter) {
		ownerEmitters.put(storeId, emitter);
		return emitter;
	}

	// 사장님 연결 삭제
	public void deleteOwnerEmitter(UUID storeId) {
		ownerEmitters.remove(storeId);
	}

	// 사장님 Emitter 조회
	public Optional<SseEmitter> findOwnerEmitter(UUID storeId) {
		return Optional.ofNullable(ownerEmitters.get(storeId));
	}
}
