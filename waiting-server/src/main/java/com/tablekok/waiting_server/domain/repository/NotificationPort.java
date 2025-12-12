package com.tablekok.waiting_server.domain.repository;

import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationPort {
	SseEmitter connect(UUID waitingId);

	void sendWaitingCall(UUID waitingId, int waitingNumber);

	void sendNoShowAlert(UUID waitingId);
}
