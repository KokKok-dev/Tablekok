package com.tablekok.store_service.domain.entity;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_store_reservation_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreReservationPolicy extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "store_reservation_policy_id", updatable = false, nullable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	// 예약 오픈 정책
	@Column(name = "monthly_open_day", nullable = false)
	private int monthlyOpenDay;

	@Column(name = "open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "reservation_interval", nullable = false)
	private int reservationInterval; // 예약 가능한 시간 간격 (분)

	// 실제 예약 가능 시간 범위 (운영 기준)
	@Column(name = "daily_reservation_start_time", nullable = false)
	private LocalTime dailyReservationStartTime;

	@Column(name = "daily_reservation_end_time", nullable = false)
	private LocalTime dailyReservationEndTime;

	// 예약 가능 인원수
	@Column(name = "min_headcount", nullable = false)
	private int minHeadcount;

	@Column(name = "max_headcount", nullable = false)
	private int maxHeadcount;

	// 선예약금 정책
	@Column(name = "is_deposit_required", nullable = false)
	private boolean isDepositRequired;

	@Column(name = "deposit_amount")
	private int depositAmount;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Builder(access = AccessLevel.PRIVATE)
	private StoreReservationPolicy(
		Store store, int monthlyOpenDay, LocalTime openTime, int reservationInterval,
		LocalTime dailyReservationStartTime,
		LocalTime dailyReservationEndTime,
		int minHeadcount, int maxHeadcount, boolean isDepositRequired, int depositAmount, boolean isActive
	) {
		this.store = store;
		this.monthlyOpenDay = monthlyOpenDay;
		this.openTime = openTime;
		this.reservationInterval = reservationInterval;
		this.dailyReservationStartTime = dailyReservationStartTime;
		this.dailyReservationEndTime = dailyReservationEndTime;
		this.minHeadcount = minHeadcount;
		this.maxHeadcount = maxHeadcount;
		this.isDepositRequired = isDepositRequired;
		this.depositAmount = depositAmount;
		this.isActive = isActive;
	}

	public static StoreReservationPolicy of(
		Store store, int monthlyOpenDay, LocalTime openTime, int reservationInterval,
		LocalTime dailyReservationStartTime,
		LocalTime dailyReservationEndTime,
		int minHeadcount, int maxHeadcount, boolean isDepositRequired, int depositAmount, boolean isActive
	) {
		return StoreReservationPolicy.builder()
			.store(store)
			.monthlyOpenDay(monthlyOpenDay)
			.openTime(openTime)
			.reservationInterval(reservationInterval)
			.dailyReservationStartTime(dailyReservationStartTime)
			.dailyReservationEndTime(dailyReservationEndTime)
			.minHeadcount(minHeadcount)
			.maxHeadcount(maxHeadcount)
			.isDepositRequired(isDepositRequired)
			.depositAmount(depositAmount)
			.isActive(isActive)
			.build();
	}

	public void softDelete(UUID deleterId) {
		super.delete(deleterId);
	}

	public void updatePolicyInfo(
		Integer monthlyOpenDay,
		LocalTime openTime,
		Integer reservationInterval,
		LocalTime dailyReservationStartTime,
		LocalTime dailyReservationEndTime,
		Integer minHeadCount,
		Integer maxHeadcount,
		Boolean isDepositRequired,
		Integer depositAmount,
		Boolean isActive
	) {
		this.monthlyOpenDay = monthlyOpenDay;
		this.openTime = openTime;
		this.reservationInterval = reservationInterval;
		this.dailyReservationStartTime = dailyReservationStartTime;
		this.dailyReservationEndTime = dailyReservationEndTime;
		this.minHeadcount = minHeadCount;
		this.maxHeadcount = maxHeadcount;
		this.isDepositRequired = isDepositRequired;
		this.depositAmount = depositAmount;
		this.isActive = isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
}
