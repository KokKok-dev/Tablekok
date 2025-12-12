package com.tablekok.waiting_server.presentation.dto.request;

import java.util.UUID;

import com.tablekok.waiting_server.application.dto.command.StartWaitingServiceCommand;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartWaitingServiceRequest(

	@NotNull(message = "매장 테이블 수는 필수입니다.")
	@Min(value = 1, message = "매장 테이블 수는 1개 이상이어야 합니다.")
	int totalTables,
	@NotNull(message = "예약 최소 인원수는 필수입니다.")
	@Min(value = 1, message = "예약 최소 인원수는 1명 이상이어야 합니다.")
	int minHeadcount,

	@NotNull(message = "예약 최대 인원수는 필수입니다.")
	@Min(value = 1, message = "예약 최대 인원수는 1명 이상이어야 합니다.")
	int maxHeadcount,

	@NotNull(message = "식사 회전율은 필수입니다.")
	@Min(value = 1, message = "최소 1분 이상입니다.")
	int turnoverRateMinutes
) {

	public StartWaitingServiceCommand toCommand(UUID storeId, UUID ownerId) {
		return StartWaitingServiceCommand.builder()
			.storeId(storeId)
			.ownerId(ownerId)
			.totalTables(totalTables)
			.minHeadCount(minHeadcount)
			.maxHeadcount(maxHeadcount)
			.turnoverRateMinutes(turnoverRateMinutes)
			.build();
	}
}
