package com.tablekok.review_service.presentation.dto.request;

import java.util.UUID;

import com.tablekok.review_service.application.dto.command.CreateReviewCommand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
	@NotNull(message = "예약 id는 필수입니다.")
	UUID reservationId,
	@NotNull(message = "별점을 입력해주세요.")
	@Min(1)
	@Max(5)
	Double rating,
	@NotBlank(message = "내용을 입력해주세요.")
	@Size(min = 10, max = 1000, message = "리뷰는 10자 이상, 1000자 이하로 작성해야합니다.")
	String content
) {
	public CreateReviewCommand toCommand() {
		return CreateReviewCommand.builder()
			.reservationId(reservationId)
			.rating(rating)
			.content(content)
			.build();
	}
}
