package com.tablekok.waiting_server.application.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tablekok.waiting_server.application.service.WaitingBatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingBatchScheduler {
	private final WaitingBatchService waitingBatchService;

	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
	public void runDailyResetTask() {
		log.info("새벽 3시 웨이팅 정기 초기화 작업을 시작합니다.");
		try {
			waitingBatchService.executeDailyReset();
			log.info("정기 초기화 작업 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			log.error("정기 초기화 작업중 오류 발생 : {}", e.getMessage());
		}
	}
}
