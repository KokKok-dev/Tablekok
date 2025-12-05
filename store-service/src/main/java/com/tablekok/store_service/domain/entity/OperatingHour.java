package com.tablekok.store_service.domain.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.tablekok.entity.BaseEntity;
import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_oprating_hour")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatingHour extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false)
	private DayOfWeek dayOfWeek;

	@Column(name = "open_time", nullable = true)
	private LocalTime openTime;

	@Column(name = "close_time", nullable = true)
	private LocalTime closeTime;

	@Column(name = "is_closed", nullable = false)
	private boolean isClosed;

	@Builder(access = AccessLevel.PRIVATE)
	private OperatingHour(
		Store store, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean isClosed
	) {
		this.dayOfWeek = dayOfWeek;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.isClosed = isClosed;
	}

	public static OperatingHour of(
		DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean isClosed
	) {
		return OperatingHour.builder()
			.dayOfWeek(dayOfWeek)
			.openTime(openTime)
			.closeTime(closeTime)
			.isClosed(isClosed)
			.build();
	}

	public void validate() {
		// 1. isClosed가 true일 경우 시간 필드는 반드시 null이어야 합니다.
		if (isClosed) {
			if (openTime != null || closeTime != null) {
				throw new AppException(StoreDomainErrorCode.INVALID_CLOSED_TIME); // TODO: StoreDomainErrorCode로 변경해야함
			}
		}
		// 2. isClosed가 false일 경우 시간 필드는 반드시 존재해야 합니다.
		else {
			if (openTime == null || closeTime == null) {
				throw new AppException(StoreDomainErrorCode.MISSING_OPERATING_TIME);
			}
			// 3. closeTime 이 openTime 이후인지 검증
			if (openTime.isAfter(closeTime)) {
				throw new AppException(StoreDomainErrorCode.INVALID_TIME_RANGE);
			}
		}
	}
}
