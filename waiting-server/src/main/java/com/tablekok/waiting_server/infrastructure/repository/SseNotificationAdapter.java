package com.tablekok.waiting_server.infrastructure.repository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.waiting_server.domain.repository.NotificationPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseNotificationAdapter implements NotificationPort {

	private final SseEmitterRepository sseEmitterRepository;
	private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000; // 1시간 타임아웃

	@Override
	public void sendWaitingCall(UUID waitingId, int waitingNumber) {
		sseEmitterRepository.findById(waitingId).ifPresent(emitter -> {
			try {
				// 이벤트 빌더를 사용하여 데이터 전송 (이벤트 이름: "waiting-call")
				emitter.send(SseEmitter.event()
					.name("waiting-call") // 클라이언트가 수신할 이벤트 이름
					.data(Map.of(
						"waitingNumber", waitingNumber,
						"message", "입장 호출! 5분 내 매장으로 와주세요. \n 5분 내로 오지 않으면 자동 취소돼요."
					))
					.id(waitingId.toString()) // 이벤트 ID (재연결 시 유용)
				);

			} catch (IOException e) {
				// 전송 실패 시 (클라이언트 연결 끊김 등) 연결 삭제
				sseEmitterRepository.deleteById(waitingId);
			}
		});
	}

	public SseEmitter connect(UUID waitingId) {
		// Emitter 생성 및 타임아웃 설정
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		// 연결 종료(타임아웃, 에러 발생) 시 Emitter 제거
		emitter.onCompletion(() -> sseEmitterRepository.deleteById(waitingId));
		emitter.onTimeout(() -> sseEmitterRepository.deleteById(waitingId));
		emitter.onError((e) -> sseEmitterRepository.deleteById(waitingId));

		// 연결 저장
		sseEmitterRepository.save(waitingId, emitter);

		// 💡 최초 연결 시 더미 데이터 전송 (연결 후 바로 데이터가 오도록 보장)
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("Connection established successfully.")
				.id(waitingId.toString())
			);
		} catch (IOException e) {
			sseEmitterRepository.deleteById(waitingId);
		}

		return emitter;
	}
}
