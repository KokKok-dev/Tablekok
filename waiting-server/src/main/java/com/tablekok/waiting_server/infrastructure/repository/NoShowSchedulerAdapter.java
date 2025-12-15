package com.tablekok.waiting_server.infrastructure.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.tablekok.waiting_server.application.port.NoShowProcessingPort;
import com.tablekok.waiting_server.application.port.NoShowSchedulerPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoShowSchedulerAdapter implements NoShowSchedulerPort {
	private final TaskScheduler taskScheduler;
	private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
	private final NoShowProcessingPort noShowProcessingPort;

	@Value("${waiting.noshow.timeout}")
	private long noShowTimeoutMinutes;

	@Override
	public void scheduleNoShowProcessing(UUID waitingId) {
		// timeout 뒤 시점을 계산
		Instant startTime = Instant.now().plus(noShowTimeoutMinutes, ChronoUnit.MINUTES);
		// Instant startTime = Instant.now().plus(noShowTimeoutMinutes, ChronoUnit.SECONDS);

		// 스케줄러에 작업 등록
		ScheduledFuture<?> future = taskScheduler.schedule(() -> {
			try {
				scheduledTasks.remove(waitingId);
				// 트랜잭션이 분리된 서비스 메서드 호출
				noShowProcessingPort.processNoShow(waitingId);
			} catch (Exception e) {
				log.error("NoShow 처리 중 오류 발생. WaitingId: {}", waitingId, e);
			}
		}, startTime);

		scheduledTasks.put(waitingId, future);
	}

	@Override
	public void cancelNoShowProcessing(UUID waitingId) {
		ScheduledFuture<?> future = scheduledTasks.remove(waitingId);

		if (future != null) {
			// 작업이 실행 중이더라도 중단 시도
			boolean cancelled = future.cancel(true);
			log.info("노쇼 스케줄링 취소 시도: WaitingId={}, 성공={}", waitingId, cancelled);
		} else {
			log.warn("취소할 예정된 노쇼 스케줄링 작업이 없습니다: WaitingId={}", waitingId);
		}
	}
}

