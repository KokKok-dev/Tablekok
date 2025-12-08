package com.tablekok.review_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.dto.result.GetReviewResult;
import com.tablekok.review_service.application.service.ReviewService;
import com.tablekok.review_service.presentation.dto.request.CreateReviewRequest;
import com.tablekok.review_service.presentation.dto.request.GetMyReviewsRequest;
import com.tablekok.review_service.presentation.dto.request.GetStoreReviewsRequest;
import com.tablekok.review_service.presentation.dto.request.UpdateReviewRequest;
import com.tablekok.review_service.presentation.dto.response.CreateReviewResponse;
import com.tablekok.review_service.presentation.dto.response.GetMyReviewsResponse;
import com.tablekok.review_service.presentation.dto.response.GetReviewResponse;
import com.tablekok.review_service.presentation.dto.response.GetStoreReviewsResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reviews")
	public ResponseEntity<ApiResponse<CreateReviewResponse>> createReview(
		@RequestBody @Valid CreateReviewRequest request
		/**@RequestHeader("XXX-USER-ID") UUID userId
		 * @RequestHeader("XXX-USER-ROLE") String role) */
	) {
		// 임시로 id 지정
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677");

		CreateReviewResult result = reviewService.createReview(request.toCommand(), userId);

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

	@PatchMapping("/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> updateReview(
		@PathVariable("reviewId") UUID reviewId,
		@RequestBody @Valid UpdateReviewRequest request
	) {
		reviewService.updateReview(reviewId, request.toCommand());
		return ResponseEntity.ok(
			ApiResponse.success("리뷰 수정이 완료되었습니다.", HttpStatus.OK));
	}

	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable("reviewId") UUID reviewId) {
		// 임시로 id 지정
		UUID userId = UUID.fromString("641f6c00-6ea3-46dc-875c-aeec53ea8677");
		reviewService.deleteReview(reviewId, userId);
		return ResponseEntity.ok(
			ApiResponse.success("리뷰 삭제가 완료되었습니다.", HttpStatus.OK));
	}

	@GetMapping("/reviews/{reviewId}")
	public ResponseEntity<ApiResponse<GetReviewResponse>> findReview(@PathVariable("reviewId") UUID reviewId) {
		GetReviewResult result = reviewService.getReview(reviewId);
		return ResponseEntity.ok(
			ApiResponse.success(
				"리뷰 조회 성공",
				GetReviewResponse.from(result),
				HttpStatus.OK
			));
	}

	@GetMapping("/stores/{storeId}/reviews")
	public ResponseEntity<ApiResponse<GetStoreReviewsResponse>> findStoreReviews(
		@PathVariable("storeId") UUID storeId,
		@ModelAttribute GetStoreReviewsRequest request
	) {

		return ResponseEntity.ok(
			ApiResponse.success(
				"리뷰 조회 성공",
				HttpStatus.OK
			));
	}

	@GetMapping("/users/me/reviews")
	public ResponseEntity<ApiResponse<GetMyReviewsResponse>> findMyReviews(
		//@RequestHeader("XXX-USER-ID") UUID userId,
		@ModelAttribute GetMyReviewsRequest request
	) {

		return ResponseEntity.ok(
			ApiResponse.success(
				"리뷰 조회 성공",
				HttpStatus.OK
			));
	}
}
