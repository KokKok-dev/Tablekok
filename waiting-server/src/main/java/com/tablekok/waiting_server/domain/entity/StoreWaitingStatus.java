package com.tablekok.waiting_server.domain.entity;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.entity.BaseEntity;
import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;
import com.tablekok.waiting_server.domain.vo.StoreInfoVo;

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

	@Column(name = "owner_id", nullable = false)
	private UUID ownerId;

	@Column(name = "store_name", nullable = false)
	private String storeName;

	@Column(name = "store_open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "store_close_time", nullable = false)
	private LocalTime closeTime;

	@Column(name = "is_waiting_enabled", nullable = false)
	private boolean isWaitingEnabled;

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
		UUID storeId, UUID ownerId, String storeName, boolean isWaitingEnabled, int totalTables,
		int latestAssignedNumber,
		int currentCallingNumber,
		int turnoverRateMinutes, int minHeadcount, int maxHeadcount) {

		this.storeId = storeId;
		this.ownerId = ownerId;
		this.storeName = storeName;
		this.isWaitingEnabled = isWaitingEnabled;
		this.totalTables = totalTables;
		this.latestAssignedNumber = latestAssignedNumber;
		this.currentCallingNumber = currentCallingNumber;
		this.turnoverRateMinutes = turnoverRateMinutes;

		this.minHeadcount = minHeadcount;
		this.maxHeadcount = maxHeadcount;

	}

	public static StoreWaitingStatus create(UUID storeId, UUID ownerId, int totalTables,
		int turnoverRateMinutes,
		int minHeadcount,
		int maxHeadcount) {

		return StoreWaitingStatus.builder()
			.storeId(storeId)
			.ownerId(ownerId)
			.isWaitingEnabled(false)
			.totalTables(totalTables)
			.latestAssignedNumber(0)
			.currentCallingNumber(0)
			.turnoverRateMinutes(turnoverRateMinutes)
			.minHeadcount(minHeadcount)
			.maxHeadcount(maxHeadcount)
			.build();
	}

	public void syncStoreInfo(StoreInfoVo vo) {
		this.ownerId = vo.ownerId();
		this.storeName = vo.storeName();
		this.openTime = vo.openTime();
		this.closeTime = vo.closeTime();
	}

	public void incrementNumber() {
		this.latestAssignedNumber += 1;
	}

	public void startWaiting(int minHeadcount, int maxHeadcount) {
		// 이미 활성화된 상태라면 예외처리
		if (this.isWaitingEnabled()) {
			throw new AppException(WaitingDomainErrorCode.WAITING_ALREADY_STARTED);
		}

		this.isWaitingEnabled = true;
		this.minHeadcount = minHeadcount;
		this.maxHeadcount = maxHeadcount;
	}

	public void stopWaiting() {
		// 이미 비활성화된 상태라면 예외처리
		if (!this.isWaitingEnabled()) {
			throw new AppException(WaitingDomainErrorCode.WAITING_ALREADY_CLOSED);
		}

		this.isWaitingEnabled = false;
	}

	public void setCurrentCallingNumber(int callingNumber) {
		this.currentCallingNumber = callingNumber;
	}

	public void validateOwner(UUID requestOwnerId) {
		if (!this.ownerId.equals(requestOwnerId)) {
			throw new AppException(WaitingDomainErrorCode.NO_STORE_OWNER);
		}
	}
}
