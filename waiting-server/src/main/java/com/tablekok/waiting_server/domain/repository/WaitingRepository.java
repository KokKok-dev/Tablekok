package com.tablekok.waiting_server.domain.repository;

import com.tablekok.waiting_server.domain.entity.Waiting;

public interface WaitingRepository {

	Waiting save(Waiting waiting);
}
