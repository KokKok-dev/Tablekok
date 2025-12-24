package com.tablekok.hotreservationservice.application.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import com.tablekok.hotreservationservice.application.service.QueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

	private final QueueService queueService;

	@Value("${reservation.available.user.limit}")
	private int availableUserLimit;

	// 예약 입장 스케쥴러
	@Scheduled(fixedRateString = "${reservation.process.interval}")
	@SchedulerLock(
		name = "hot-reservation-scheduler-lock",
		lockAtMostFor = "10s",
		lockAtLeastFor = "500ms"
	)
	public void processReservations() {

		// 예약 허용 시간 초과 유저 삭제 및 입장 인원 반환
		int usersToProcess = queueService.processExpiredUsers();

		log.info("예약 가능 수용 인원 : {}, 예약 입장 가능한 인원 : {}", availableUserLimit, usersToProcess);
		if (usersToProcess > 0) {
			// 예약 입장 및 순번 번경 알림
			queueService.processAllUsers(usersToProcess);
		}
	}

}
