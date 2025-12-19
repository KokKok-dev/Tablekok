package com.tablekok.review_service.application.dto;

public record ReviewStatsDto(
	Double averageRating,
	Long reviewCount
) {
}
