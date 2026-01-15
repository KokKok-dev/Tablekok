package com.tablekok.reservation_service.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import com.tablekok.reservation_service.application.dto.result.GetReservedTimeResult;

import lombok.Builder;

@Builder
public record GetReservedTimeResponse(
	List<LocalTime> reservedTimes
) {
	public static GetReservedTimeResponse fromResult(GetReservedTimeResult result) {
		return GetReservedTimeResponse.builder()
			.reservedTimes(result.reservedTimes())
			.build();
	}
}
