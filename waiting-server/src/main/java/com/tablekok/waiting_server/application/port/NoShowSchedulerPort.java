package com.tablekok.waiting_server.application.port;

import java.util.UUID;

public interface NoShowSchedulerPort {
	void scheduleNoShowProcessing(UUID waitingId, long delayMinutes);
}
