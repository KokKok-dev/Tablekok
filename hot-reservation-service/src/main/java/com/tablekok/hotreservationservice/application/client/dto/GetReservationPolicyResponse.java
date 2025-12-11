package com.tablekok.hotreservationservice.application.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tablekok.hotreservationservice.domain.vo.ReservationPolicy;

public record GetReservationPolicyResponse(
	Boolean enable,        // 예약 가능 여부
	Integer maxPeople,  // 예약 최대 인원
	Integer minPeople,    // 예약 최소 인원
	LocalDate openDate,    // 다음 달 예약이 풀리는 일
	LocalTime openTime  // 다음 달 예약이 풀리는 시간
) {
	public static ReservationPolicy toVo(GetReservationPolicyResponse response) {
		return ReservationPolicy.of(response.enable, response.maxPeople, response.minPeople, response.openDate,
			response.openTime);
	}
}
