package com.tablekok.waiting_server.domain.entity;

import java.util.UUID;

import com.tablekok.entity.BaseEntity;
import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_store_waiting_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreWaitingStatus extends BaseEntity {
	@Id
	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "is_open_for_waiting", nullable = false)
	private boolean isOpenForWaiting;

	@Column(name = "total_tables", nullable = false)
	private int totalTables;

	@Column(name = "latest_assigned_number", nullable = false)
	private int latestAssignedNumber = 0; // 마지막으로 발급된 웨이팅 번호

	@Column(name = "current_calling_number", nullable = false)
	private int currentCallingNumber = 0; // 현재 매장에서 호출 중인 가장 최근 대기 번호

	@Column(name = "turnoverRateMinutes", nullable = false)
	private int turnoverRateMinutes;

	@Column(name = "min_headcount", nullable = false)
	private int minHeadcount;

	@Column(name = "max_headcount", nullable = false)
	private int maxHeadcount;

	@Builder(access = AccessLevel.PRIVATE)
	private StoreWaitingStatus(
		UUID storeId, boolean isOpenForWaiting, int totalTables, int latestAssignedNumber, int currentCallingNumber,
		int turnoverRateMinutes, int minHeadcount, int maxHeadcount) {

		this.storeId = storeId;
		this.isOpenForWaiting = isOpenForWaiting;
		this.totalTables = totalTables;
		this.latestAssignedNumber = latestAssignedNumber;
		this.currentCallingNumber = currentCallingNumber;
		this.turnoverRateMinutes = turnoverRateMinutes;

		this.minHeadcount = minHeadcount;
		this.maxHeadcount = maxHeadcount;

	}

	public static StoreWaitingStatus create(UUID storeId, int totalTables, int turnoverRateMinutes, int minHeadcount,
		int maxHeadcount) {

		return StoreWaitingStatus.builder()
			.storeId(storeId)
			.isOpenForWaiting(true)
			.totalTables(totalTables)
			.latestAssignedNumber(0)
			.currentCallingNumber(0)
			.turnoverRateMinutes(turnoverRateMinutes)
			.minHeadcount(minHeadcount)
			.maxHeadcount(maxHeadcount)
			.build();
	}

	public void incrementNumber() {
		this.latestAssignedNumber += 1;
	}

	public void startWaiting(int minHeadcount, int maxHeadcount) {
		// 이미 활성화된 상태라면 예외처리
		if (this.isOpenForWaiting()) {
			throw new AppException(WaitingDomainErrorCode.WAITING_ALREADY_STARTED);
		}

		this.isOpenForWaiting = true;
		this.minHeadcount = minHeadcount;
		this.maxHeadcount = maxHeadcount;
	}

	public void stopWaiting() {
		// 이미 비활성화된 상태라면 예외처리
		if (!this.isOpenForWaiting()) {
			throw new AppException(WaitingDomainErrorCode.WAITING_ALREADY_CLOSED);
		}

		this.isOpenForWaiting = false;
	}

	public void setCurrentCallingNumber(int callingNumber) {
		this.currentCallingNumber = callingNumber;
	}
}
