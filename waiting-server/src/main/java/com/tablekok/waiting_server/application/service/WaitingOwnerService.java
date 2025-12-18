package com.tablekok.waiting_server.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.client.StoreClient;
import com.tablekok.waiting_server.application.dto.command.StartWaitingServiceCommand;
import com.tablekok.waiting_server.application.dto.result.GetWaitingQueueResult;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.application.port.NoShowSchedulerPort;
import com.tablekok.waiting_server.application.port.NotificationPort;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;
import com.tablekok.waiting_server.domain.repository.WaitingCachePort;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingOwnerService {
	private final StoreWaitingStatusRepository storeWaitingStatusRepository;
	private final WaitingRepository waitingRepository;
	private final NotificationPort notificationPort;
	private final NoShowSchedulerPort noShowSchedulerPort;
	private final WaitingCachePort waitingCache;
	private final StoreClient storeClient;

	@Transactional
	public void startWaitingService(StartWaitingServiceCommand command) {
		Optional<StoreWaitingStatus> existingStatus = findStoreWaitingStatus(command.storeId());

		// 레코드가 없는 경우 (최초 생성 및 초기화)
		if (existingStatus.isEmpty()) {
			// 사장님이 storeId의 실제 소유자인지 확인 (feign 호출)
			UUID ownerId = storeClient.getStoreOwner(command.storeId());
			if (!ownerId.equals(command.ownerId())) {
				throw new AppException(WaitingErrorCode.NO_STORE_OWNER);
			}

			StoreWaitingStatus newStatus = command.toEntity(command.ownerId());
			storeWaitingStatusRepository.save(newStatus);
			return;
		}

		// 레코드가 이미 존재하는 경우 (운영 스위치만 ON)
		StoreWaitingStatus status = existingStatus.get();
		status.startWaiting(command.minHeadCount(), command.maxHeadcount());
	}

	@Transactional
	public void stopWaitingService(UUID storeId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인

		StoreWaitingStatus status = getStoreWaitingStatus(storeId);
		status.stopWaiting();
	}

	@Transactional(readOnly = true)
	public List<GetWaitingQueueResult> getStoreWaitingQueue(UUID storeId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인

		// Redis ZSET에서 현재 대기 중인 모든 waitingId를 가져옴
		List<String> waitingIdStrings = waitingCache.getWaitingIds(storeId);
		if (waitingIdStrings.isEmpty()) {
			return Collections.emptyList();
		}

		List<UUID> waitingIds = waitingIdStrings.stream()
			.map(UUID::fromString)
			.toList();

		//  waitingIds 로 RDB에서 waitings 조회
		List<Waiting> waitings = waitingRepository.findAllByIdIn(waitingIds);

		// Redis의 순서를 보장하며 DTO로 변환
		return mapToRankedQueueResults(waitingIdStrings, waitings);
	}

	@Transactional
	public void callWaiting(UUID storeId, UUID callingWaitingId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		Waiting waiting = findWaiting(callingWaitingId);
		StoreWaitingStatus status = getStoreWaitingStatus(storeId);

		// 상태를 WAITING -> CALLED으로 변경
		waiting.callCustomer();

		// StoreWaitingStatus 업데이트 (currentCallingNumber)
		status.setCurrentCallingNumber(waiting.getWaitingNumber());

		// DB 상태가 커밋된 이후에 알람, 스케줄러 등록
		registerPostCommitActions(callingWaitingId, waiting.getWaitingNumber());
	}

	@Transactional
	public void enterWaiting(UUID storeId, UUID waitingId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		Waiting waiting = findWaitingForStore(waitingId, storeId);
		WaitingStatus originalStatus = waiting.getStatus();

		// Waiting 엔티티의 상태를 ENTERED 변경, 입장 시간(enteredAt) 기록
		waiting.enter();
		waitingRepository.save(waiting);

		// WaitingId Redis ZSET 에서 제거
		waitingCache.removeWaiting(storeId, waitingId.toString());

		//  노쇼 자동 처리 타이머를 중단
		cancelNoShowTimerIfActive(waitingId, originalStatus);

		// StoreWaitingStatus의 currentCallingNumber를 waitingNumber로 업데이트
		updateCurrentCallingNumber(storeId, waiting.getWaitingNumber());

		// DB 상태가 커밋된 이후에 알람
		registerPostEnterActions(waitingId, storeId);
	}

	@Transactional
	public void cancelByOwner(UUID storeId, UUID waitingId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인

		Waiting waiting = findWaitingForStore(waitingId, storeId);
		WaitingStatus originalStatus = waiting.getStatus();

		// Waiting 엔티티의 상태를 OWNER_CANCELED 변경
		waiting.cancelByOwner();
		waitingRepository.save(waiting);

		// WaitingId Redis ZSET 에서 제거
		waitingCache.removeWaiting(storeId, waitingId.toString());

		// 만약 상태가 CALLED 또는 CONFIRM, 노쇼 자동 처리 타이머를 중단
		cancelNoShowTimerIfActive(waitingId, originalStatus);

		// 고객에게 웨이팅이 취소되었음을 알림
		registerPostOwnerCancelActions(waitingId);
	}

	@Transactional
	public void markNoShow(UUID storeId, UUID waitingId, UUID ownerId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인

		Waiting waiting = findWaitingForStore(waitingId, storeId);
		WaitingStatus originalStatus = waiting.getStatus();

		// Waiting 엔티티의 상태를 NO_SHOW로 변경
		waiting.noShow();
		waitingRepository.save(waiting);

		// WaitingId Redis ZSET 에서 제거
		waitingCache.removeWaiting(storeId, waitingId.toString());

		// 만약 상태가 CALLED 또는 CONFIRM, 노쇼 자동 처리 타이머를 중단
		cancelNoShowTimerIfActive(waitingId, originalStatus);

		// 고객에게 노쇼 처리되었음을 알림
		registerPostMarkNoShowActions(waitingId);
	}

	private Optional<StoreWaitingStatus> findStoreWaitingStatus(UUID storeId) {
		return storeWaitingStatusRepository.findById(storeId);
	}

	private Waiting findWaiting(UUID waitingId) {
		return waitingRepository.findById(waitingId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.WAITING_NOT_FOUND));
	}

	private Waiting findWaitingForStore(UUID waitingId, UUID storeId) {
		return waitingRepository.findByIdAndStoreId(waitingId, storeId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.WAITING_NOT_IN_STORE));
	}

	private StoreWaitingStatus getStoreWaitingStatus(UUID storeId) {
		return storeWaitingStatusRepository.findById(storeId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.STORE_WAITING_STATUS_NOT_FOUND));
	}

	private void registerPostCommitActions(UUID waitingId, int callingNumber) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// 호출 알림
				notificationPort.sendWaitingCall(waitingId, callingNumber);
				// 스케줄러 등록
				noShowSchedulerPort.scheduleNoShowProcessing(waitingId);
			}
		});
	}

	public SseEmitter connectOwnerWaitingNotification(UUID storeId) {
		return notificationPort.connectOwner(storeId);
	}

	private void registerPostMarkNoShowActions(UUID waitingId) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// 고객에게 노쇼 처리되었음을 알림
				notificationPort.sendNoShowAlert(waitingId);
			}
		});
	}

	private void cancelNoShowTimerIfActive(UUID waitingId, WaitingStatus originalStatus) {
		// 웨이팅의 원본 상태가 CALLED 또는 CONFIRMED인 경우,  노쇼 자동 처리 타이머 중단
		if (originalStatus == WaitingStatus.CALLED || originalStatus == WaitingStatus.CONFIRMED) {
			noShowSchedulerPort.cancelNoShowProcessing(waitingId);
		}
	}

	private void updateCurrentCallingNumber(UUID storeId, int waitingNumber) {
		StoreWaitingStatus status = getStoreWaitingStatus(storeId);
		status.setCurrentCallingNumber(waitingNumber);
		storeWaitingStatusRepository.save(status);
	}

	private void registerPostEnterActions(UUID waitingId, UUID storeId) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// 고객에게 입장 처리되었음을 알림 (SSE 연결 종료)
				notificationPort.sendEnteredAlert(waitingId);

				// 사장님에게 큐 상태 업데이트 알림
				notificationPort.sendOwnerQueueUpdate(storeId);
			}
		});
	}

	private void registerPostOwnerCancelActions(UUID waitingId) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// 고객에게 취소 알림
				notificationPort.sendOwnerCancelAlert(waitingId);

			}
		});
	}

	private List<GetWaitingQueueResult> mapToRankedQueueResults(
		List<String> orderedWaitingIdStrings,
		List<Waiting> waitings
	) {
		// 1. RDB 결과를 Map으로 변환
		Map<UUID, Waiting> waitingMap = waitings.stream()
			.collect(Collectors.toMap(Waiting::getId, Function.identity()));

		List<GetWaitingQueueResult> results = new ArrayList<>();
		int rank = 1;

		// 2. Redis 순서대로 반복하며 맵에서 조회 및 순위 부여
		for (String waitingIdStr : orderedWaitingIdStrings) {
			UUID waitingId = UUID.fromString(waitingIdStr);
			Waiting waiting = waitingMap.get(waitingId);

			if (waiting != null) {
				results.add(GetWaitingQueueResult.of(waiting, rank));
			}
			rank++;
		}

		return results;
	}

}
