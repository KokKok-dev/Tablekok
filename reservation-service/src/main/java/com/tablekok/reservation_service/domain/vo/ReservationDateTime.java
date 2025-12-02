package com.tablekok.reservation_service.domain.vo;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationDateTime {
	private LocalDate reservationDate;
	private LocalTime reservationTime;

	public static ReservationDateTime of(LocalDate reservationDate, LocalTime reservationTime) {
		return ReservationDateTime.builder()
			.reservationDate(reservationDate)
			.reservationTime(reservationTime)
			.build();
	}
}
