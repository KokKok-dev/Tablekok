package com.tablekok.waiting_server.presentation.dto.request;

import java.util.UUID;

import com.tablekok.waiting_server.application.dto.command.CreateWaitingCommand;
import com.tablekok.waiting_server.domain.entity.CustomerType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWaitingRequest(
	@NotNull(message = "인원수는 필수 입력 사항입니다.")
	@Min(value = 1, message = "인원수는 최소 1명 이상이어야 합니다.")
	Integer headcount,

	// 비회원 전용 필드 (회원인 경우 null)
	@Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
	String nonMemberName,

	@Size(max = 20, message = "연락처는 20자를 초과할 수 없습니다.")
	String nonMemberPhone
) {

	public CreateWaitingCommand toCommand(UUID storeId, UUID authenticatedMemberId) {
		CustomerType customerType;
		UUID finalMemberId;

		// 1. 로그인 여부 확인
		if (authenticatedMemberId != null) {
			// 회원 로그인 상태
			customerType = CustomerType.MEMBER;
			finalMemberId = authenticatedMemberId;
		} else {
			// 비회원 상태
			customerType = CustomerType.NON_MEMBER;
			finalMemberId = null;
		}

		return CreateWaitingCommand.builder()
			.storeId(storeId)
			.headcount(headcount)
			.customerType(customerType) // 결정된 타입 주입
			.memberId(finalMemberId)    // 결정된 memberId 주입
			.nonMemberName(nonMemberName)
			.nonMemberPhone(nonMemberPhone)
			.build();
	}
}
