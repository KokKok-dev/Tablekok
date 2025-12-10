package com.tablekok.waiting_server.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.waiting_server.application.dto.command.CreateWaitingCommand;
import com.tablekok.waiting_server.application.dto.result.CreateWaitingResult;
import com.tablekok.waiting_server.application.dto.result.GetWaitingResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingService {

	// @Transactional
	public CreateWaitingResult createWaiting(CreateWaitingCommand command) {
		// TODO: 매장 접수 가능 상태 확인 (StoreClient - waitingOpenTime 조회)
		// TODO: 고객 웨이팅 중복 확인
		// TODO: 인원수 유효성 확인

		// TODO: 다음웨이팅 번호 발급 (StoreWaitingStatus에서 latest_assigned_number 증가, 새로운번호 확보)

		// TODO: WaitingQueue 엔티티 생성 후 저장
		// TODO: 발급된 번호(Score)와 WaitingId를 Redis ZSET에 등록

		// TODO: Redis ZSET에서 현재 등록된 모든 웨이팅 팀 수(ZCARD)와 본인의 순위(ZRANK)를 조회
		// TODO: (현재 대기 팀 수) * (팀당 평균 소요 시간) 공식을 사용하여 estimatedWaitMinutes를 계산
		// TODO: CreateWaitingResult DTO를 반환

		UUID newWaitingId = UUID.randomUUID(); // 새로 생성된 웨이팅 ID
		int assignedNumber = 105; // 발급된 번호
		int rank = 3; // Redis ZRANK 결과
		int totalTeams = 5; // Redis ZCARD 결과
		int estimatedTime = 25; // 계산된 예상 시간

		// 2. of() 메서드를 사용하여 결과 객체 생성
		return CreateWaitingResult.of(
			newWaitingId,
			command.storeId(),
			assignedNumber,
			rank,
			totalTeams,
			estimatedTime,
			"WAITING",
			LocalDateTime.now()
		);
	}

	public GetWaitingResult getWaiting(UUID waitingId) {
		// TODO: waitingId로 웨이팅 기록 조회
		// TODO: 매장 ID 및 상태 확인 (웨이팅을 받고 있는지)
		// TODO: Redis 순위, 팀 수, 예상 시간 조회/계산

		UUID dummyStoreId = UUID.fromString("1a1b1c1d-1111-2222-3333-1234567890ab");

		return GetWaitingResult.of(
			waitingId,
			dummyStoreId,
			105,              // waitingNumber (DB에서 가져옴)
			3,                // currentRank (Redis에서 계산)
			5,                // currentWaitingTeams (Redis에서 계산)
			25,               // estimatedWaitMinutes (계산)
			"WAITING",        // status (DB에서 가져옴)
			LocalDateTime.now().minusMinutes(10) // queuedAt (DB에서 가져옴)
		);
	}
}
