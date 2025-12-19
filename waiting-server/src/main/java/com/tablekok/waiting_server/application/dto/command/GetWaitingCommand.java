package com.tablekok.waiting_server.application.dto.command;

import java.util.UUID;

import lombok.Builder;

@Builder
public record GetWaitingCommand(
	UUID waitingId,
	UUID memberId,
	String nonMemberName,
	String nonMemberPhone
) {
	public static GetWaitingCommand of(
		UUID waitingId,
		UUID memberId,
		String nonMemberName,
		String nonMemberPhone
	) {
		return GetWaitingCommand.builder()
			.waitingId(waitingId)
			.memberId(memberId)
			.nonMemberName(nonMemberName)
			.nonMemberPhone(nonMemberPhone)
			.build();
	}
}
