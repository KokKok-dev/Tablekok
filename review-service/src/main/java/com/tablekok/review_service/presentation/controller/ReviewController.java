package com.tablekok.review_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.service.ReviewService;
import com.tablekok.review_service.presentation.dto.request.CreateReviewRequest;
import com.tablekok.review_service.presentation.dto.response.CreateReviewResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateReviewResponse>> createReview(
		@RequestBody @Valid CreateReviewRequest request
		/**@RequestHeader("XXX-USER-ID") UUID userId
		 * @RequestHeader("XXX-USER-ROLE") String role) */
	) {
		// 임시로 id 지정
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677");

		CreateReviewResult result = reviewService.createReview(request.toParam(), userId);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{reviewId}")
			.buildAndExpand(result.reviewId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success(
				"리뷰 생성이 완료되었습니다.",
				CreateReviewResponse.from(result),
				HttpStatus.CREATED
			));
	}


}
