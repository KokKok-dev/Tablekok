package com.tablekok.waiting_server.application.dto.command;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CreateWaitingCommand(
	UUID storeId,

	Integer headcount,
	String nonMemberName,
	String nonMemberPhone

) {
}
