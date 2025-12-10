package com.tablekok.waiting_server.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record GetWaitingQueueResult(
	UUID waitingId,
	Integer waitingNumber,
	String status,
	LocalDateTime queuedAt,

	// 2. 사장님께 필요한 추가 정보
	String customerType,        // MEMBER 또는 NON_MEMBER
	Integer headcount,          // 인원수 (테이블 할당 기준)

	// 3. 연락 정보 (비회원일 경우만 값이 채워짐)
	String nonMemberName,
	String nonMemberPhone
) {
}
