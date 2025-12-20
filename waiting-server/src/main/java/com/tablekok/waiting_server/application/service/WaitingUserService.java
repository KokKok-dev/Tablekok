package com.tablekok.waiting_server.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.dto.command.CreateWaitingCommand;
import com.tablekok.waiting_server.application.dto.command.GetWaitingCommand;
import com.tablekok.waiting_server.application.dto.result.CreateWaitingResult;
import com.tablekok.waiting_server.application.dto.result.GetWaitingResult;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.application.port.NoShowSchedulerPort;
import com.tablekok.waiting_server.application.port.NotificationPort;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;
import com.tablekok.waiting_server.domain.repository.WaitingCachePort;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;
import com.tablekok.waiting_server.domain.service.WaitingUserDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingUserService {

	private final WaitingCachePort waitingCache;
	private final StoreWaitingStatusRepository storeWaitingStatusRepository;
	private final WaitingRepository waitingRepository;
	private final WaitingUserDomainService waitingUserDomainService;
	private final NotificationPort notificationPort;
	private final NoShowSchedulerPort noShowSchedulerPort;

	@Transactional
	public CreateWaitingResult createWaiting(CreateWaitingCommand command) {
		StoreWaitingStatus status = findStoreWaitingStatus(command.storeId());

		// 접수 가능 상태 확인 (스위치 ON 여부 + 영업 시간 확인)
		status.validateAcceptingWaiting();

		waitingUserDomainService.validateHeadcountPolicy(command.headcount(), status.getMinHeadcount(),
			status.getMaxHeadcount()); // 인원수 유효성 검사
		// 고객 웨이팅 중복 확인
		waitingUserDomainService.validateDuplicateWaiting(
			command.storeId(),
			command.customerType(),
			command.memberId(),
			command.nonMemberPhone()
		);

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
		int estimatedTime = waitingUserDomainService.calculateEstimateWaitMinutes(rank, status);

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

	@Transactional(readOnly = true)
	public GetWaitingResult getWaiting(GetWaitingCommand command) {
		Waiting waiting = findWaiting(command.waitingId());

		// Member ID가 일치하지 않으면 권한 없음
		waitingUserDomainService.validateAccessPermission(waiting, command.memberId(), command.nonMemberName(),
			command.nonMemberPhone());

		// 매장 ID 및 상태 확인 (회전식사시간 확인)
		StoreWaitingStatus status = findStoreWaitingStatus(waiting.getStoreId());

		// Redis 앞에 대기팀 수, 예상 시간 조회/계산
		Long rankZeroBased = waitingCache.getRank(waiting.getStoreId(), waiting.getId().toString());
		int rank = 0;
		int estimatedTime = 0;

		// 예상 대기 시간 조회
		// CALLED, CONFIRMED, ENTERED, NO_SHOW, CANCEL 상태일 때는 0을 반환
		if (waiting.getStatus() == WaitingStatus.WAITING) {
			rank = (rankZeroBased != null) ? rankZeroBased.intValue() + 1 : 1;

			// 예상 대기 시간 계산
			estimatedTime = waitingUserDomainService.calculateEstimateWaitMinutes(rank, status);
		}

		return GetWaitingResult.of(
			command.waitingId(),
			waiting.getStoreId(),
			waiting.getWaitingNumber(),
			rank,
			estimatedTime,
			waiting.getStatus().name(),
			waiting.getQueuedAt()
		);
	}

	@Transactional
	public void confirmWaiting(GetWaitingCommand command) {
		Waiting waiting = findWaiting(command.waitingId());

		waitingUserDomainService.validateAccessPermission(waiting, command.memberId(), command.nonMemberName(),
			command.nonMemberPhone());

		// entity 상태 변경 (CALLED -> CONFIRMED)
		waiting.confirmByUser();

		// 호출(CALLED) 시점부터 시작된 노쇼 자동 처리 타이머를 즉시 중단(취소)
		noShowSchedulerPort.cancelNoShowProcessing(command.waitingId());

		// 고객이 입장을 확정했음을 사장님(Store Owner)에게 실시간으로 알림 & 다시 5분 내로 입장해야함.
		registerPostConfirmActions(command.waitingId(), waiting.getWaitingNumber(), waiting.getStoreId());
	}

	@Transactional
	public void cancelWaiting(GetWaitingCommand command) {
		Waiting waiting = findWaiting(command.waitingId());

		waitingUserDomainService.validateAccessPermission(waiting, command.memberId(), command.nonMemberName(),
			command.nonMemberPhone());

		// USER_CANCELED 상태변경
		waiting.cancelByUser();

		// Redis ZSET에서 제거
		waitingCache.removeWaiting(waiting.getStoreId(), command.waitingId().toString());

		// CALLED 상태였다면 노쇼 타이머 중단
		if (waiting.getStatus() == WaitingStatus.CALLED) {
			noShowSchedulerPort.cancelNoShowProcessing(command.waitingId());
		}

	}

	public SseEmitter connectUserWaitingNotification(GetWaitingCommand command) {
		Waiting waiting = findWaiting(command.waitingId());

		// Member ID가 일치하지 않으면 권한 없음
		waitingUserDomainService.validateAccessPermission(waiting, command.memberId(), command.nonMemberName(),
			command.nonMemberPhone());

		return notificationPort.connectCustomer(command.waitingId());
	}

	private StoreWaitingStatus findStoreWaitingStatus(UUID storeId) {
		return storeWaitingStatusRepository.findById(storeId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.STORE_WAITING_STATUS_NOT_FOUND));
	}

	private Waiting findWaiting(UUID waitingId) {
		return waitingRepository.findById(waitingId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.WAITING_NOT_FOUND));
	}

	private void registerPostConfirmActions(UUID waitingId, int callingNumber, UUID storeId) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// 사장님한테 confirm 했다고 알림
				notificationPort.sendWaitingConfirmed(waitingId, callingNumber, storeId);
				// 스케줄러 등록
				noShowSchedulerPort.scheduleNoShowProcessing(waitingId);
			}
		});
	}
}
