package com.tablekok.waiting_server.application.dto.command;

import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.CustomerType;
import com.tablekok.waiting_server.domain.entity.Waiting;

import lombok.Builder;

@Builder
public record CreateWaitingCommand(
	UUID storeId,

	int headcount,
	CustomerType customerType,
	UUID memberId,
	String nonMemberName,
	String nonMemberPhone

) {

	public Waiting toEntity(int waitingNumber) {
		return Waiting.create(storeId, waitingNumber, customerType, memberId, nonMemberName, nonMemberPhone, headcount);
	}
}
