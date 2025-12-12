package com.tablekok.waiting_server.application.dto.command;

import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

import lombok.Builder;

@Builder
public record StartWaitingServiceCommand(

	UUID storeId,
	UUID ownerId,

	int minHeadCount,

	int maxHeadcount,

	int turnoverRateMinutes
) {

	public StoreWaitingStatus toEntity() {
		return StoreWaitingStatus.create(storeId, turnoverRateMinutes, minHeadCount, maxHeadcount);
	}
}
