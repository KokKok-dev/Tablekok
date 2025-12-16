package com.tablekok.waiting_server.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.domain.entity.CustomerType;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.entity.WaitingStatus;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;
import com.tablekok.waiting_server.domain.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingUserDomainService {

	private final WaitingRepository waitingRepository;

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
		boolean exists = false;

		// 현재 활성화 되어있는 웨이팅 조회하기 위해 상태 정보 필요
		List<WaitingStatus> activeStatuses = List.of(
			WaitingStatus.WAITING,
			WaitingStatus.CALLED,
			WaitingStatus.CONFIRMED
		);

		if (customerType == CustomerType.MEMBER) {
			// --- [회원 중복 확인 로직] ---
			if (memberId == null) {
				throw new AppException(WaitingDomainErrorCode.MEMBER_ID_REQUIRED);
			}

			exists = waitingRepository.existsByStoreIdAndMemberIdAndStatusIn(
				storeId, memberId, activeStatuses
			);

			if (exists) {
				throw new AppException(WaitingDomainErrorCode.DUPLICATE_MEMBER_WAITING);
			}

		} else if (customerType == CustomerType.NON_MEMBER) {
			// --- [비회원 중복 확인 로직] ---
			if (nonMemberPhone == null || nonMemberPhone.isEmpty()) {
				throw new AppException(WaitingDomainErrorCode.PHONE_NUMBER_REQUIRED);
			}

			exists = waitingRepository.existsByStoreIdAndNonMemberPhoneAndStatusIn(
				storeId, nonMemberPhone, activeStatuses
			);

			if (exists) {
				// 중복 확인 시, 비회원은 전화번호만 사용
				throw new AppException(WaitingDomainErrorCode.DUPLICATE_MEMBER_WAITING);
			}
		} else {
			throw new AppException(WaitingDomainErrorCode.INVALID_CUSTOMER_TYPE);
		}

	}
}
