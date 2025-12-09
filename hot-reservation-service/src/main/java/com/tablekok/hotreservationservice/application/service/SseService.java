package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseService {

	// 사용자 ID를 키로, SseEmitter를 값으로 저장하여 관리
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Value("${queue.sse.timeout}")
	private Long TIMEOUT;

	// 새로운 SSE 연결을 등록합니다.
	// @param userId 사용자 ID
	// @return SseEmitter 객체
	public SseEmitter addEmitter(String userId, Long rank) {
		// 이미 연결된 Emitter가 있다면 닫고 새로 생성
		if (emitters.containsKey(userId)) {
			emitters.get(userId).complete();
			emitters.remove(userId);
		}

		SseEmitter emitter = new SseEmitter(TIMEOUT);
		this.emitters.put(userId, emitter);

		// Emitter가 만료되거나 완료되면 Map에서 제거
		emitter.onCompletion(() -> this.emitters.remove(userId));
		emitter.onTimeout(() -> {
			log.warn("SSE Emitter Timeout: {}", userId);
			emitter.complete();
			this.emitters.remove(userId);
		});

		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data(rank + 1)
				.id(userId));
		} catch (IOException e) {
			log.error("Failed to send initial message to {}", userId, e);
		}

		return emitter;
	}

	// 특정 사용자에게 메시지를 전송합니다.
	// @param userId 사용자 ID
	// @param eventName 이벤트 이름 (예: "rank", "status")
	// @param data 전송할 데이터
	public void send(String userId, String eventName, Object data) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event()
					.name(eventName)
					.data(data)
					.id(userId + "_" + System.currentTimeMillis()));
			} catch (IOException e) {
				// 전송 실패 시, Emitter를 제거하고 연결을 종료
				log.warn("Failed to send SSE to {}. Closing emitter.", userId);
				emitter.completeWithError(e);
				emitters.remove(userId);
			}
		}
	}

	// Emitter 연결 명시적 종료
	public void completeEmitter(String userId) {
		SseEmitter emitter = emitters.remove(userId);
		if (emitter != null) {
			emitter.complete();
		}
	}
}
