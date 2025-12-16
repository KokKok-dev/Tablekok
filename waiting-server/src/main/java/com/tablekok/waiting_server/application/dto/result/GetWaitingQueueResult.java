package com.tablekok.waiting_server.application.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tablekok.waiting_server.domain.entity.CustomerType;
import com.tablekok.waiting_server.domain.entity.Waiting;

import lombok.Builder;

@Builder
public record GetWaitingQueueResult(
	UUID waitingId,
	Integer rank,
	Integer waitingNumber,
	String status,
	LocalDateTime queuedAt,

	// 2. 사장님께 필요한 추가 정보
	CustomerType customerType,        // MEMBER 또는 NON_MEMBER
	Integer headcount,          // 인원수 (테이블 할당 기준)

	// 3. 연락 정보 (비회원일 경우만 값이 채워짐)
	String nonMemberName,
	String nonMemberPhone
) {

	public static GetWaitingQueueResult of(Waiting waiting, int rank) {
		CustomerType customerType = waiting.getCustomerType();
		boolean isNonMember = customerType == CustomerType.NON_MEMBER;

		String name = isNonMember ? waiting.getNonMemberName() : null;
		String phone = isNonMember ? waiting.getNonMemberPhone() : null;

		return GetWaitingQueueResult.builder()
			.waitingId(waiting.getId())
			.rank(rank)
			.waitingNumber(waiting.getWaitingNumber())
			.status(waiting.getStatus().name())
			.queuedAt(waiting.getQueuedAt())

			// 사장님께 필요한 추가 정보
			.customerType(customerType)
			.headcount(waiting.getHeadcount())

			// 연락 정보
			.nonMemberName(name)
			.nonMemberPhone(phone)
			.build();
	}
}
