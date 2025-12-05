package com.tablekok.store_service.domain.entity;

import java.time.LocalTime;
import java.util.UUID;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_reservation_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationPolicy extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

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

}
