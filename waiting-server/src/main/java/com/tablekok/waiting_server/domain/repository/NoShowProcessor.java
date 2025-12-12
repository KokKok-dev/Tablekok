package com.tablekok.waiting_server.domain.repository;

import java.util.UUID;

public interface NoShowProcessor {
	void processNoShow(UUID waitingId);
}
