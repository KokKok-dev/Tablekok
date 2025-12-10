package com.tablekok.waiting_server.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.waiting_server.application.dto.result.GetWaitingQueueResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingOwnerService {
	public List<GetWaitingQueueResult> getStoreWaitingQueue(UUID storeId) {
		// TODO: Redis ZSET에서 현재 대기 중인 모든 waitingId를 가져와 RDB에서 상세 정보를 조회하는 로직으로 대체

		LocalDateTime now = LocalDateTime.now();
		return List.of(
			new GetWaitingQueueResult(
				UUID.randomUUID(), 101, "CALLED", now.minusMinutes(40), "MEMBER", 2, "김철수", "010-1234-5678"
			),
			new GetWaitingQueueResult(UUID.randomUUID(), 102, "WAITING", now.minusMinutes(25), "NON_MEMBER", 4, "이영희",
				"010-9876-5432"
			),
			new GetWaitingQueueResult(UUID.randomUUID(), 102, "WAITING", now.minusMinutes(25), "NON_MEMBER", 4, "이영희",
				"010-9876-5432"
			)
		);
	}
}
