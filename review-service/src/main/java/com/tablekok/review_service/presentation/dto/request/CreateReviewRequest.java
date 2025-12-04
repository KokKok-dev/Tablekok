package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.review_service.application.dto.param.CreateReviewParam;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
	@NotNull(message = "예약 id는 필수입니다.")
	UUID reservationId,
	@NotNull(message = "별점을 입력해주세요.")
	@Min(1)
	@Max(5)
	Double rating,
	@NotBlank(message = "내용을 입력해주세요.")
	String content
) {
	public CreateReviewParam toParam() {
		return CreateReviewParam.builder()
			.reservationId(reservationId)
			.rating(rating)
			.content(content)
			.build();
	}
}
