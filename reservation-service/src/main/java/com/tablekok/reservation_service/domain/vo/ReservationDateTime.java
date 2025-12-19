package com.tablekok.reservation_service.domain.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

	public static ReservationDateTime of(LocalDateTime reservationDateTime) {
		return ReservationDateTime.builder()
			.reservationDate(reservationDateTime.toLocalDate())
			.reservationTime(reservationDateTime.toLocalTime())
			.build();
	}
}
