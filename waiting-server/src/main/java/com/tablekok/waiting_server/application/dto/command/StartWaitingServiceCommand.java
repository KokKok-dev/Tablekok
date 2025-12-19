package com.tablekok.waiting_server.application.dto.command;

import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;

import lombok.Builder;

@Builder
public record StartWaitingServiceCommand(

	UUID storeId,
	UUID ownerId,

	int totalTables,

	int minHeadCount,

	int maxHeadcount,

	int turnoverRateMinutes
) {

	public StoreWaitingStatus toEntity(UUID ownerId) {
		return StoreWaitingStatus.create(storeId, ownerId, totalTables, turnoverRateMinutes, minHeadCount,
			maxHeadcount);
	}
}
