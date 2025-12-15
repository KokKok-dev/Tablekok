package com.tablekok.waiting_server.application.port;

import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationPort {
	SseEmitter connect(UUID waitingId);

	void sendWaitingCall(UUID waitingId, int waitingNumber);

	void sendNoShowAlert(UUID waitingId);

	void sendWaitingConfirmed(UUID waitingId, int waitingNumber, UUID storeId);
}
