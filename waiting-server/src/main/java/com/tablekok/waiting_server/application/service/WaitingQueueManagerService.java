package com.tablekok.waiting_server.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.waiting_server.application.port.NoShowProcessingPort;
import com.tablekok.waiting_server.application.port.NotificationPort;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.repository.WaitingCachePort;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingQueueManagerService implements NoShowProcessingPort {
	private final WaitingRepository waitingRepository;
	private final WaitingCachePort waitingCache;
	private final NotificationPort notificationPort;

	// 타이머 만료 시 no_show 상태로
	@Transactional
	public void processNoShow(UUID waitingId) {
		Waiting waiting = waitingRepository.findById(waitingId).orElse(null);

		// 여전히 CALLED 또는 CONFIRMED 상태일 때만 노쇼 처리
		if (waiting != null) {
			if (waiting.getStatus() == WaitingStatus.CALLED || waiting.getStatus() == WaitingStatus.CONFIRMED) {
				waiting.noShow();

				// cache에서 waitingQueue 삭제
				waitingCache.removeWaiting(waiting.getStoreId(), waitingId.toString());

				// Noshow 알림
				notificationPort.sendNoShowAlert(waitingId);

				// 사장님에게 큐 상태 변경 알림
				notificationPort.sendOwnerQueueUpdate(waiting.getStoreId());
			}
		}
	}

}
