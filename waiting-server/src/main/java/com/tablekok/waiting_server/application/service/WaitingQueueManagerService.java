package com.tablekok.waiting_server.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.repository.NoShowProcessor;
import com.tablekok.waiting_server.domain.repository.NoShowSchedulerPort;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingQueueManagerService implements NoShowProcessor {
	private final WaitingRepository waitingRepository;
	private final NoShowSchedulerPort noShowSchedulerPort;
	final long NO_SHOW_TIMEOUT_MINUTES = 5;

	// 타이머 등록
	public void scheduleNoShow(UUID waitingId) {
		noShowSchedulerPort.scheduleNoShowProcessing(waitingId, NO_SHOW_TIMEOUT_MINUTES);
	}

	// 타이머 만료 시 no_show 상태로
	@Transactional
	public void processNoShow(UUID waitingId) {
		Waiting waiting = waitingRepository.findById(waitingId).orElse(null);

		if (waiting != null && waiting.getStatus() == WaitingStatus.CALLED) {
			waiting.noShow();
			waitingRepository.save(waiting);

			// TODO: cache에서 waiting 삭제
			// TODO: Noshow 알림

			// TODO: 매장 관리자에게 알림
		}
	}

}
