package com.tablekok.reservation_service.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.tablekok.reservation_service.application.dto.result.GetReservationsForCustomerResult;

import lombok.Builder;

@Builder
public record GetReservationsForCustomerResponse(
	UUID userId,
	UUID storeId,
	String reservationNumber,
	LocalDate reservationDate,
	LocalTime reservationTime,
	Integer headcount,
	Integer deposit,
	String reservationStatus
) {
	public static Page<GetReservationsForCustomerResponse> fromResult(Page<GetReservationsForCustomerResult> results) {
		return results.map(result -> GetReservationsForCustomerResponse.builder()
			.userId(result.userId())
			.storeId(result.storeId())
			.reservationNumber(result.reservationNumber())
			.reservationDate(result.reservationDate())
			.reservationTime(result.reservationTime())
			.headcount(result.headcount())
			.deposit(result.deposit())
			.reservationStatus(result.reservationStatus())
			.build()
		);

	}
}
