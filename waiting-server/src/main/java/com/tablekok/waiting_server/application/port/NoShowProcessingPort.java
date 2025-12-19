package com.tablekok.waiting_server.application.port;

import java.util.UUID;

public interface NoShowProcessingPort {
	void processNoShow(UUID waitingId);
}
