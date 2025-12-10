package com.tablekok.waiting_server.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateWaitingResult(
	UUID waitingId,         // 생성된 웨이팅의 고유 ID (향후 조회, 취소, 응답에 사용됨)
	UUID storeId,           // 웨이팅이 등록된 매장 ID

	// 2. 웨이팅 현황 정보
	Integer waitingNumber,  // 발급받은 대기 번호 (예: 105번)
	Integer currentRank,    // 현재 나의 대기 순서 (예: 3번째 팀)
	Integer currentWaitingTeams, // 현재 전체 대기 중인 팀 수 (예: 5팀)
	Integer estimatedWaitMinutes, // 예상 대기 시간 (분 단위)

	// 3. 상태 정보
	String status,          // 현재 웨이팅 상태 (WAITING)
	LocalDateTime queuedAt
) {
	public static CreateWaitingResult of(
		UUID waitingId,
		UUID storeId,
		Integer waitingNumber,
		Integer currentRank,
		Integer currentWaitingTeams,
		Integer estimatedWaitMinutes,
		String status,
		LocalDateTime queuedAt
	) {
		return CreateWaitingResult.builder()
			.waitingId(waitingId)
			.storeId(storeId)
			.waitingNumber(waitingNumber)
			.currentRank(currentRank)
			.currentWaitingTeams(currentWaitingTeams)
			.estimatedWaitMinutes(estimatedWaitMinutes)
			.status(status)
			.queuedAt(queuedAt)
			.build();
	}
}
