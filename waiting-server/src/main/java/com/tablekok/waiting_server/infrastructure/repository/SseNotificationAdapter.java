package com.tablekok.waiting_server.infrastructure.repository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.waiting_server.application.port.NotificationPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseNotificationAdapter implements NotificationPort {

	private final SseEmitterRepository sseEmitterRepository;
	private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000; // 1시간 타임아웃

	@Override
	public void sendWaitingCall(UUID waitingId, int waitingNumber) {
		sseEmitterRepository.findCustomerEmitter(waitingId).ifPresent(emitter -> {
			try {
				// 이벤트 빌더를 사용하여 데이터 전송 (이벤트 이름: "waiting-call")
				emitter.send(SseEmitter.event()
					.name("waiting-call") // 클라이언트가 수신할 이벤트 이름
					.data(Map.of(
						"waitingNumber", waitingNumber,
						"message", "입장 호출! 10분 내 매장으로 와주세요. \n 10분 내로 오지 않으면 자동 취소돼요."
					))
					.id(waitingId.toString()) // 이벤트 ID (재연결 시 유용)
				);

			} catch (IOException e) {
				// 전송 실패 시 (클라이언트 연결 끊김 등) 연결 삭제
				log.error("SSE 전송 실패 (waiting-call). WaitingId: {}", waitingId, e);
				sseEmitterRepository.deleteCustomerEmitter(waitingId);
			}
		});
	}

	@Override
	public void sendNoShowAlert(UUID waitingId) {
		sseEmitterRepository.findCustomerEmitter(waitingId).ifPresent(emitter -> {
			try {
				// 이벤트 빌더를 사용하여 데이터 전송 (이벤트 이름: "waiting-call")
				emitter.send(SseEmitter.event()
					.name("waiting-noshow-timeout") // 클라이언트가 수신할 이벤트 이름
					.data(Map.of(
						"newStatus", "NO_SHOW",
						"message", "호출 응답 시간이 초과되어 자동 노쇼 처리되었습니다."
					))
					.id(waitingId.toString()) // 이벤트 ID (재연결 시 유용)
				);

			} catch (IOException e) {
				// 전송 실패 시 (클라이언트 연결 끊김 등) 연결 삭제
				log.error("SSE 전송 실패 (waiting-noshow-timeout). WaitingId: {}", waitingId, e);
				sseEmitterRepository.deleteCustomerEmitter(waitingId);
			}
		});
	}

	@Override
	public void sendWaitingConfirmed(UUID waitingId, int waitingNumber, UUID storeId) {
		sseEmitterRepository.findOwnerEmitter(storeId).ifPresent(emitter -> {
			try {
				// 2. 이벤트 빌더를 사용하여 데이터 전송
				emitter.send(SseEmitter.event()
					.name("waiting-confirmed") // 사장님 분클라이언트가 수신할 이벤트 이름
					.data(Map.of(
						"waitingId", waitingId.toString(),
						"waitingNumber", waitingNumber,
						"newStatus", "CONFIRMED",
						"message", waitingNumber + "번 손님이 웨이팅을 확정했습니다. 10분 내로 매장 근처로 온다면 '입장'으로 상태를 변경하세요."
					))
					// 사장님은 여러 웨이팅 이벤트를 받으므로, ID는 storeId와 이벤트 타입 조합으로 설정 가능
					.id(storeId.toString() + "-" + waitingId)
				);

			} catch (IOException e) {
				// 전송 실패 시 (사장님 클라이언트 연결 끊김 등) 연결 삭제
				log.error("SSE 전송 실패 (waiting-confirmed). StoreId: {}", storeId, e);
				sseEmitterRepository.deleteOwnerEmitter(storeId);
			}
		});
	}

	@Override
	public void sendOwnerQueueUpdate(UUID storeId) {
		sseEmitterRepository.findOwnerEmitter(storeId).ifPresent(emitter -> {
			try {
				emitter.send(SseEmitter.event()
					.name("queue-update")
					.data(Map.of("message", "노쇼 처리로 인해 웨이팅 큐가 변경되었습니다."))
				);
			} catch (IOException e) {
				log.error("사장님 SSE 알림 전송 실패: StoreId={}", storeId, e);
				sseEmitterRepository.deleteOwnerEmitter(storeId);
			}
		});
	}

	@Override
	public SseEmitter connectCustomer(UUID waitingId) {
		// Emitter 생성 및 타임아웃 설정
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		// 연결 종료(타임아웃, 에러 발생) 시 Emitter 제거
		emitter.onCompletion(() -> sseEmitterRepository.deleteCustomerEmitter(waitingId));
		emitter.onTimeout(() -> sseEmitterRepository.deleteCustomerEmitter(waitingId));
		emitter.onError((e) -> sseEmitterRepository.deleteCustomerEmitter(waitingId));

		// 연결 저장
		sseEmitterRepository.saveCustomerEmitter(waitingId, emitter);

		// 최초 연결 시 더미 데이터 전송 (연결 후 바로 데이터가 오도록 보장)
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("Connection established successfully.")
				.id(waitingId.toString())
			);
		} catch (IOException e) {
			log.error("SSE 최초 연결 데이터 전송 실패. WaitingId: {}", waitingId, e);
			sseEmitterRepository.deleteCustomerEmitter(waitingId);
		}

		return emitter;
	}

	@Override
	public SseEmitter connectOwner(UUID storeId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

		// Emitter 종료 핸들러 설정: deleteOwnerEmitter 사용
		emitter.onCompletion(() -> sseEmitterRepository.deleteOwnerEmitter(storeId));
		emitter.onTimeout(() -> sseEmitterRepository.deleteOwnerEmitter(storeId));
		emitter.onError((e) -> sseEmitterRepository.deleteOwnerEmitter(storeId));

		// 연결 저장: saveOwnerEmitter 사용
		sseEmitterRepository.saveOwnerEmitter(storeId, emitter);

		// 최초 연결 시 더미 데이터 전송
		try {
			emitter.send(SseEmitter.event()
				.name("owner-connect")
				.data("Owner connection established successfully. StoreId: " + storeId.toString())
			);
		} catch (IOException e) {
			log.error("SSE 사장님 연결 데이터 전송 실패. StoreId: {}", storeId, e);
			sseEmitterRepository.deleteOwnerEmitter(storeId);
		}

		return emitter;
	}
}
