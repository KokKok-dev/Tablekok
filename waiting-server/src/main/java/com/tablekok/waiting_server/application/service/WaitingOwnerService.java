package com.tablekok.waiting_server.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.dto.command.StartWaitingServiceCommand;
import com.tablekok.waiting_server.application.dto.result.GetWaitingQueueResult;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingOwnerService {
	private final StoreWaitingStatusRepository storeWaitingStatusRepository;
	private final WaitingRepository waitingRepository;

	@Transactional
	public void startWaitingService(StartWaitingServiceCommand command) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		Optional<StoreWaitingStatus> existingStatus = findStoreWaitingStatus(command.storeId());

		// 레코드가 없는 경우 (최초 생성 및 초기화)
		if (existingStatus.isEmpty()) {
			StoreWaitingStatus newStatus = command.toEntity();
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
		Optional<StoreWaitingStatus> existingStatus = findStoreWaitingStatus(storeId);
		// 해당 매장의 웨이팅 시스템이 아예 설정된 적이 없음.
		if (existingStatus.isEmpty()) {
			throw new AppException(WaitingErrorCode.STORE_WAITING_STATUS_NOT_FOUND);
		}

		StoreWaitingStatus status = existingStatus.get();
		status.stopWaiting();
	}

	public List<GetWaitingQueueResult> getStoreWaitingQueue(UUID storeId) {
		// TODO: Redis ZSET에서 현재 대기 중인 모든 waitingId를 가져와 RDB에서 상세 정보를 조회하는 로직으로 대체

		LocalDateTime now = LocalDateTime.now();
		return List.of(
			new GetWaitingQueueResult(
				UUID.randomUUID(), 101, "CALLED", now.minusMinutes(40), "MEMBER", 2, "김철수", "010-1234-5678"
			),
			new GetWaitingQueueResult(UUID.randomUUID(), 102, "WAITING", now.minusMinutes(25), "NON_MEMBER", 4, "이영희",
				"010-9876-5432"
			),
			new GetWaitingQueueResult(UUID.randomUUID(), 102, "WAITING", now.minusMinutes(25), "NON_MEMBER", 4, "이영희",
				"010-9876-5432"
			)
		);
	}

	public void callWaiting(UUID storeId, UUID waitingId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		// waitingId 조회하여 상태가 WAITING 상태인지 확인
		Waiting waiting = findWaiting(waitingId);
		if (waiting.getStatus() != WaitingStatus.WAITING) {
			throw new AppException(WaitingErrorCode.INVALID_WAITING_STATUS);
		}

		// StoreWaitingStatus 업데이트 (currentCallingNumber)
		StoreWaitingStatus status = storeWaitingStatusRepository.findById(storeId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.STORE_WAITING_STATUS_NOT_FOUND));
		int callingNumber = waiting.getWaitingNumber();
		status.setCurrentCallingNumber(callingNumber);

		// 상태를 WAITING -> CALLING으로 변경
		waiting.callCustomer();
		waitingRepository.save(waiting);

		// TODO: 입장 호출 알림 푸시 후, 5분 내에 응답(confirm)하지 않으면 노쇼 처리한다는 내용 전달

		// TODO: 5분 뒤에 실행되 ㄹ노쇼 자동 처리 배치/스케줄러 작업 등록

		// TODO: StoreWaitingStatus의 currentCallingNumber를 호출된 고객의 waitingNumber로 업데이트

		// TODO: 남은 대기 고객들에게 현재 호출 번호가 변경
	}

	public void enterWaiting(UUID storeId, UUID waitingId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		// TODO: waitingId 조회하여 상태가 CALLED인지 CONFIRMED 상태인지 확인 -> NO_SHOW 여도 바꿀수 있게 할까

		// TODO: WaitingQueue 엔티티의 상태를 ENTERED 변경, 입장 시간(enteredAt) 기록
		// TODO: WaitingId Redis ZSET 에서 제거

		// TODO: 만약 상태가 CALLED였다면, 이전에 시작된 노쇼 자동 처리 타이머를 중단
		// TODO: StoreWaitingStatus의 currentCallingNumber를 waitingNumber로 업데이트

		// TODO: 남은 대기 고객들에게 순위 변경 알림
	}

	public void cancelByOwner(UUID storeId, UUID waitingId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		// TODO: waitingId 조회하여 상태가 이미 ENTERED 등 최종 상태가 아닌지 확인

		// TODO: WaitingQueue 엔티티의 상태를 OWNER_CANCELED 변경
		// TODO: WaitingId Redis ZSET 에서 제거

		// TODO: 만약 상태가 CALLED였다면, 노쇼 자동 처리 타이머를 중단
		// TODO: 고객에게 웨이팅이 취소되었음을 알림
		// TODO: 남은 대기 고객들에게 순위가 변경되었음을 알림
	}

	public void markNoShow(UUID storeId, UUID waitingId) {
		// TODO: 사장님이 storeId의 실제 소유자인지 확인
		// TODO: waitingId 조회하여 상태가 CALLED 또는 CONFIRMED 상태인지 확인

		// TODO: WaitingQueue 엔티티의 상태를 NO_SHOW로 변경
		// TODO: WaitingId Redis ZSET 에서 제거

		// TODO: 노쇼 자동 처리 타이머가 혹시라도 남아있다면 중단
		// TODO: 고객에게 노쇼 처리되었음을 알립
		// TODO: 남은 대기 고객들에게 순위가 변경되었음을 알림
	}

	private Optional<StoreWaitingStatus> findStoreWaitingStatus(UUID storeId) {
		return storeWaitingStatusRepository.findById(storeId);
	}

	private Waiting findWaiting(UUID waitingId) {
		return waitingRepository.findById(waitingId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.WAITING_NOT_FOUND));
	}
}
