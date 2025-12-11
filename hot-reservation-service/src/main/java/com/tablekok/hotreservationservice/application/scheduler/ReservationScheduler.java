package com.tablekok.hotreservationservice.application.scheduler;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.application.service.QueueService;
import com.tablekok.hotreservationservice.application.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

	private final QueueService queueService;
	private final SseService sseService;

	@Value("${reservation.limit.count}")
	private int reservationLimit;

	// 예약 가능 인원 제한에 따라 대기열 사용자 처리
	// 예약 가능 시간이 만료된 사용자 처리
	@Scheduled(fixedRateString = "${reservation.process.interval}")
	public void processReservations() {
		long now = Instant.now().toEpochMilli();

		// 현재 예약 진행 중인 사용자 중에 예약 허용 시간이 초과된 사람 강퇴
		processExpiredUsers(now);

		// 현재 예약 진행 중인 사용자 수 확인
		int currentAvailableCount = queueService.getAvailableUsers().size();

		// 새로 예약 가능 상태로 전환할 수 있는 인원 수 계산
		int usersToProcess = reservationLimit - currentAvailableCount;

		log.info("현재 예약 진행 중인 사용자 : {}, 예약 입장 가능한 인원 : {}", currentAvailableCount, usersToProcess);
		if (usersToProcess > 0) {
			// 예약 입장 및 순번 번경 알림
			processAllUsers(usersToProcess);
		}
	}

	// 현재 예약 진행 중인 사용자 중에 예약 허용 시간이 초과된 사람 강퇴
	private void processExpiredUsers(long now) {
		// 예약 가능 상태 사용자 목록을 조회
		queueService.getAvailableUsers().forEach((userIdObj, expirationTimeObj) -> {
			String userId = userIdObj.toString();
			long expirationTime = Long.parseLong(expirationTimeObj.toString());

			// 만료 시간 초과 시
			if (now >= expirationTime) {
				log.info("Reservation token expired for user: {}", userId);

				// Redis에서 상태 삭제 (다음 대기열 사람을 받을 수 있게 됨)
				queueService.completeReservation(userId);
				sseService.send(userId, "expired", "예약 가능 시간이 만료되어 대기열로 다시 진입해야 합니다.");
			}
		});
	}

	private void processAllUsers(int count) {
		// 모든 대기자 id 조회
		Set<String> allUsers = queueService.getAllUsers();

		int index = 0;
		for (String userId : allUsers) { // 스케쥴러로 항상 모든 유저를 조회..? 흠..
			if (index < count) {
				// 예약 가능자 처리
				String token = queueService.issueTokenAndRegister(userId);

				queueService.removeUserFromQueue(userId);

				sseService.send(userId, "enter", token);
				index++;
				continue;
			}

			// 나머지 사용자 순번 변경 알림. 몇명 입장 했는지 전달
			sseService.send(userId, "update", count);
			index++;
		}

	}

}
