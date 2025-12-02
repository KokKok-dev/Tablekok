package com.tablekok.store_service.presentation.dto.request;

import java.time.LocalTime;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReservationPolicyRequest(
	@Range(min = 1, max = 31, message = "예약 오픈 날짜는 1일부터 31일 사이여야 합니다.")
	int monthlyOpenDay,

	@NotNull(message = "예약 오픈 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime openTime,

	@Min(value = 1, message = "예약 간격은 최소 1분 이상이어야 합니다.")
	int reservationInterval,

	@NotNull(message = "예약 받을 시작 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime dailyReservationStartTime,

	@NotNull(message = "예약 마감 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime dailyReservationEndTime,

	@Min(value = 1, message = "예약 최소 인원수는 1명 이상이어야 합니다.")
	int minHeadCount,

	@Min(value = 1, message = "예약 최대 인원수는 1명 이상이어야 합니다.")
	int maxHeadcount,

	boolean isDepositRequired,

	@Min(value = 0, message = "예약금은 0원 이상이어야 합니다.")
	int depositAmount,

	boolean isActive

) {
}
