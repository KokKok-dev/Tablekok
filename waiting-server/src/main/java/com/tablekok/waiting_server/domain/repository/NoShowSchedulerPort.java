package com.tablekok.waiting_server.domain.repository;

import java.util.UUID;

public interface NoShowSchedulerPort {
	void scheduleNoShowProcessing(UUID waitingId, long delayMinutes);
}
