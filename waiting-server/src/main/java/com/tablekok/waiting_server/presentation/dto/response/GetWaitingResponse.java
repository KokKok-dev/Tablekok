package com.tablekok.waiting_server.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.waiting_server.application.dto.result.GetWaitingResult;

import lombok.Builder;

@Builder
public record GetWaitingResponse(
	UUID waitingId,         // 생성된 웨이팅의 고유 ID (향후 조회, 취소, 응답에 사용됨)
	UUID storeId,           // 웨이팅이 등록된 매장 ID

	Integer waitingNumber,  // 발급받은 대기 번호 (예: 105번)
	Integer currentRank,    // 현재 나의 대기 순서 (예: 3번째 팀)
	Integer estimatedWaitMinutes, // 예상 대기 시간 (분 단위)

	String status,          // 현재 웨이팅 상태 (WAITING)
	LocalDateTime queuedAt  // 웨이팅 등록 시간
) {

	public static GetWaitingResponse from(GetWaitingResult result) {
		return GetWaitingResponse.builder()
			.waitingId(result.waitingId())
			.storeId(result.storeId())
			.waitingNumber(result.waitingNumber())
			.currentRank(result.currentRank())
			.estimatedWaitMinutes(result.estimatedWaitMinutes())
			.status(result.status())
			.queuedAt(result.queuedAt())
			.build();
	}
}
