package com.tablekok.store_service.presentation.dto.request;

import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import com.tablekok.store_service.application.dto.command.CreateStoreReservationPolicyCommand;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateStoreReservationPolicyRequest(
	@NotNull(message = "예약 오픈 날짜는 필수입니다.")
	@Range(min = 1, max = 28, message = "예약 오픈 날짜는 1일부터 31일 사이여야 합니다.")
	Integer monthlyOpenDay,

	@NotNull(message = "예약 오픈 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime openTime,

	@NotNull(message = "예약 간격은 필수입니다.")
	@Min(value = 1, message = "예약 간격은 최소 1분 이상이어야 합니다.")
	Integer reservationInterval,

	@NotNull(message = "예약 받을 시작 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime dailyReservationStartTime,

	@NotNull(message = "예약 마감 시간은 필수입니다.")
	@DateTimeFormat(pattern = "HH:mm")
	LocalTime dailyReservationEndTime,

	@NotNull(message = "예약 최소 인원수는 필수입니다.")
	@Min(value = 1, message = "예약 최소 인원수는 1명 이상이어야 합니다.")
	Integer minHeadCount,

	@NotNull(message = "예약 최대 인원수는 필수입니다.")
	@Min(value = 1, message = "예약 최대 인원수는 1명 이상이어야 합니다.")
	Integer maxHeadcount,

	@NotNull(message = "예약금 필수 여부는 필수입니다.")
	Boolean isDepositRequired,

	@NotNull(message = "예약금은 필수입니다.")
	@Min(value = 0, message = "예약금은 0원 이상이어야 합니다.")
	Integer depositAmount,

	@NotNull(message = "정책 활성화 여부는 필수입니다.")
	Boolean isActive

) {

	public CreateStoreReservationPolicyCommand toCommand(UUID storeId) {
		return CreateStoreReservationPolicyCommand.builder()
			.storeId(storeId)
			.monthlyOpenDay(monthlyOpenDay)
			.openTime(openTime)
			.reservationInterval(reservationInterval)
			.dailyReservationStartTime(dailyReservationStartTime)
			.dailyReservationEndTime(dailyReservationEndTime)
			.minHeadCount(minHeadCount)
			.maxHeadcount(maxHeadcount)
			.isDepositRequired(isDepositRequired)
			.depositAmount(depositAmount)
			.isActive(isActive)
			.build();
	}
}
