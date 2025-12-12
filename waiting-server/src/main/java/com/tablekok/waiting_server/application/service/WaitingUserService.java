package com.tablekok.waiting_server.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.dto.command.CreateWaitingCommand;
import com.tablekok.waiting_server.application.dto.result.CreateWaitingResult;
import com.tablekok.waiting_server.application.dto.result.GetWaitingResult;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;
import com.tablekok.waiting_server.domain.repository.WaitingCachePort;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingUserService {

	private final WaitingCachePort waitingCache;
	private final StoreWaitingStatusRepository storeWaitingStatusRepository;
	private final WaitingRepository waitingRepository;

	@Transactional
	public CreateWaitingResult createWaiting(CreateWaitingCommand command) {
		// TODO: 매장 접수 가능 상태 확인 (StoreClient - waitingOpenTime 조회)

		// 다음웨이팅 번호 발급 (StoreWaitingStatus에서 latest_assigned_number 증가, 새로운번호 확보)
		StoreWaitingStatus status = storeWaitingStatusRepository.findByIdWithLock(command.storeId())
			.orElseThrow(() -> new AppException(WaitingErrorCode.STORE_WAITING_STATUS_NOT_FOUND));

		validateStoreStatus(status); // status 활성화 확인
		validateHeadcountPolicy(command.headcount(), status.getMinHeadcount(), status.getMaxHeadcount()); // 인원수 유효성 검사
		// TODO: 고객 웨이팅 중복 확인

		status.incrementNumber();
		int assignedNumber = status.getLatestAssignedNumber();

		// Waiting 엔티티 생성 후 저장
		Waiting newWaiting = command.toEntity(assignedNumber);
		UUID newWaitingId = newWaiting.getId();
		Waiting savedWaiting = waitingRepository.save(newWaiting);

		// 발급된 번호(Score)와 WaitingId를 Redis ZSET에 등록
		waitingCache.addWaiting(
			command.storeId(),
			assignedNumber,
			newWaitingId.toString()
		);

		// Redis ZSET에서 본인의 순위(ZRANK)를 조회
		Long rankZeroBased = waitingCache.getRank(command.storeId(), newWaitingId.toString());
		int rank = (rankZeroBased != null) ? rankZeroBased.intValue() + 1 : 1;

		// ((현재 대기 팀 수) / (테이블 수))* (팀당 평균 소요 시간) 공식을 사용하여 estimatedWaitMinutes를 계산
		int estimatedTime = calculateEstimateWaitMinutes(rank, status);

		// CreateWaitingResult DTO를 반환
		return CreateWaitingResult.of(
			newWaitingId,
			command.storeId(),
			assignedNumber,
			rank,
			estimatedTime,
			savedWaiting.getStatus().name(), // WAITING
			savedWaiting.getQueuedAt()
		);
	}

	private void validateStoreStatus(StoreWaitingStatus status) {
		if (!status.isOpenForWaiting()) {
			throw new AppException(WaitingErrorCode.WAITING_CLOSED);
		}
	}

	private void validateHeadcountPolicy(int headcount, int min, int max) {
		if (headcount < min) {
			throw new AppException(WaitingErrorCode.HEADCOUNT_BELOW_MIN);
		}

		if (headcount > max) {
			throw new AppException(WaitingErrorCode.HEADCOUNT_ABOVE_MAX);
		}
	}

	private int calculateEstimateWaitMinutes(int rank, StoreWaitingStatus status) {
		int teamsAhead = rank - 1;
		int totalTables = status.getTotalTables();
		int requiredTableTurns = (teamsAhead + totalTables - 1) / totalTables;

		return requiredTableTurns * status.getTurnoverRateMinutes();
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

	public void confirmWaiting(UUID waitingId) {
		// TODO: waitingId를 사용하여 WaitingQueue를 조회
		// TODO: 고객 본인 확인
		// TODO: 현재 상태가 반드시 CALLED인지 확인
		// TODO: entity 상태 변경 (CALLED -> CONFIRMED)

		// TODO: 호출 시점부터 시작된 노쇼 자동 처리 타이머를 즉시 중단(취소)
		// TODO: 고객이 입장을 확정했음을 사장님(Store Owner)에게 실시간으로 알림
	}

	public void cancelWaiting(UUID waitingId) {
		// TODO: waitingId를 사용하여 WaitingQueue를 조회
		// TODO: 고객 본인 확인
		// TODO: entity 상태 변경 ( -> USER_CANCELED)
		// TODO: Redis ZSET에서 제거
		// TODO: 노쇼 타이머 중단 (필요시)
		
	}
}
