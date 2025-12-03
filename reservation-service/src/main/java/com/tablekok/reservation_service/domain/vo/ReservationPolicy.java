package com.tablekok.reservation_service.domain.vo;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class ReservationPolicy {
	private Boolean enable;         // 예약 가능 여부
	private Integer maxPeople;   // 예약 최대 인원
	private Integer minPeople;     // 예약 최소 인원
	private LocalDate openDate;     // 다음 달 예약이 풀리는 일
	private LocalTime openTime;  // 다음 달 예약이 풀리는 시간

	public static ReservationPolicy of(Boolean enable, Integer maxPeople, Integer minPeople, LocalDate openDate,
		LocalTime openTime) {
		return ReservationPolicy.builder()
			.enable(enable)
			.maxPeople(maxPeople)
			.minPeople(minPeople)
			.openDate(openDate)
			.openTime(openTime)
			.build();
	}
}
