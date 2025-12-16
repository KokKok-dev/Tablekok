package com.tablekok.waiting_server.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.domain.entity.CustomerType;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.Waiting;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingUserDomainService {

	private final WaitingRepository waitingRepository;
	// 현재 활성화 되어있는 웨이팅 조회하기 위해 상태 정보 필요
	private static final List<WaitingStatus> ACTIVE_STATUSES = List.of(
		WaitingStatus.WAITING,
		WaitingStatus.CALLED,
		WaitingStatus.CONFIRMED
	);

	public void validateStoreStatus(StoreWaitingStatus status) {
		if (!status.isOpenForWaiting()) {
			throw new AppException(WaitingDomainErrorCode.WAITING_CLOSED);
		}
	}

	public void validateHeadcountPolicy(int headcount, int min, int max) {
		if (headcount < min) {
			throw new AppException(WaitingDomainErrorCode.HEADCOUNT_BELOW_MIN);
		}

		if (headcount > max) {
			throw new AppException(WaitingDomainErrorCode.HEADCOUNT_ABOVE_MAX);
		}
	}

	public int calculateEstimateWaitMinutes(int rank, StoreWaitingStatus status) {
		int teamsAhead = rank - 1;
		int totalTables = status.getTotalTables();
		int requiredTableTurns = (teamsAhead + totalTables - 1) / totalTables;

		return requiredTableTurns * status.getTurnoverRateMinutes();
	}

	public void validateDuplicateWaiting(UUID storeId, CustomerType customerType, UUID memberId,
		String nonMemberPhone) {

		// 유효하지 않은 CustomerType인 경우 예외 처리
		if (customerType != CustomerType.MEMBER && customerType != CustomerType.NON_MEMBER) {
			throw new AppException(WaitingDomainErrorCode.INVALID_CUSTOMER_TYPE);
		}

		if (customerType == CustomerType.MEMBER) {
			if (memberId == null) {
				throw new AppException(WaitingDomainErrorCode.MEMBER_ID_REQUIRED);
			}

			boolean exists = waitingRepository.existsByStoreIdAndMemberIdAndStatusIn(
				storeId, memberId, ACTIVE_STATUSES
			);

			if (exists) { // 이미 웨이팅 등록이 되어있다면
				throw new AppException(WaitingDomainErrorCode.DUPLICATE_MEMBER_WAITING);
			}
		}

		if (customerType == CustomerType.NON_MEMBER) {
			if (nonMemberPhone == null || nonMemberPhone.isEmpty()) {
				throw new AppException(WaitingDomainErrorCode.PHONE_NUMBER_REQUIRED);
			}

			boolean exists = waitingRepository.existsByStoreIdAndNonMemberPhoneAndStatusIn(
				storeId, nonMemberPhone, ACTIVE_STATUSES
			);

			if (exists) { // 이미 웨이팅 등록이 되어있다면
				throw new AppException(WaitingDomainErrorCode.DUPLICATE_NON_MEMBER_WAITING);
			}
		}
	}

	public void validateAccessPermission(Waiting waiting, UUID memberId, String nonMemberName, String nonMemberPhone) {
		// 유효하지 않은 CustomerType인 경우 예외 처리
		if (!waiting.isMember() && !waiting.isNonMember()) {
			throw new AppException(WaitingErrorCode.INVALID_CUSTOMER_TYPE);
		}

		if (waiting.isMember()) {
			// memberId가 없거나 불일치하면 예외 발생
			if (!waiting.getMemberId().equals(memberId)) {
				throw new AppException(WaitingErrorCode.CONNECT_FORBIDDEN);
			}
		}

		if (waiting.isNonMember()) {
			// 이름과 전화번호 중 하나라도 불일치하면 예외 발생
			if (!waiting.getNonMemberName().equals(nonMemberName) ||
				!waiting.getNonMemberPhone().equals(nonMemberPhone)) {
				throw new AppException(WaitingErrorCode.CONNECT_FORBIDDEN);
			}
		}
	}
}
