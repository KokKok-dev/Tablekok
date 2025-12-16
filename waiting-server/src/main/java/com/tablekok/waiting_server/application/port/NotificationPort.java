package com.tablekok.waiting_server.application.port;

import java.util.UUID;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationPort {
	SseEmitter connectCustomer(UUID waitingId);

	SseEmitter connectOwner(UUID storeId);

	void sendWaitingCall(UUID waitingId, int waitingNumber);

	void sendNoShowAlert(UUID waitingId);

	void sendWaitingConfirmed(UUID waitingId, int waitingNumber, UUID storeId);

	void sendOwnerQueueUpdate(UUID storeId);

	void sendOwnerCancelAlert(UUID waitingId);

	void sendEnteredAlert(UUID waitingId);
}
