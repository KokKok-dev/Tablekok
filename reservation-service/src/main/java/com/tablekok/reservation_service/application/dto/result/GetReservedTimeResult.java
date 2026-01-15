package com.tablekok.reservation_service.application.dto.result;

import java.time.LocalTime;
import java.util.List;

import com.tablekok.reservation_service.domain.entity.Reservation;

import lombok.Builder;

@Builder
public record GetReservedTimeResult(
	List<LocalTime> reservedTimes
) {
	public static GetReservedTimeResult of(List<Reservation> reservations) {
		List<LocalTime> times = reservations.stream().map(reservation ->
			reservation.getReservationDateTime().getReservationTime()
		).toList();

		return GetReservedTimeResult.builder()
			.reservedTimes(times)
			.build();
	}
}
