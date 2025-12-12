package com.tablekok.waiting_server.domain.service;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;

@Service
public class WaitingUserDomainService {

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
}
