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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.cursor.dto.response.Cursor;
import com.tablekok.dto.ApiResponse;
import com.tablekok.review_service.application.dto.result.CreateReviewResult;
import com.tablekok.review_service.application.dto.result.GetReviewResult;
import com.tablekok.review_service.application.service.ReviewService;
import com.tablekok.review_service.presentation.dto.request.CreateReviewRequest;
import com.tablekok.review_service.presentation.dto.request.GetMyReviewCursorRequest;
import com.tablekok.review_service.presentation.dto.request.GetStoreReviewCursorRequest;
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
		@RequestBody @Valid CreateReviewRequest request,
		@RequestHeader("X-User-Id") String strUserId
	) {
		// 임시로 id 지정
		UUID userId = UUID.fromString(strUserId);

		CreateReviewResult result = reviewService.createReview(request.toCommand(), userId);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{reviewId}")
			.buildAndExpand(result.reviewId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success(
				"리뷰가 등록되었습니다.",
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
	public ResponseEntity<ApiResponse<Void>> deleteReview(
		@PathVariable("reviewId") UUID reviewId,
		@RequestHeader("X-User-Id") String strUserId
	) {
		// 임시로 id 지정
		UUID userId = UUID.fromString(strUserId);
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
	public ResponseEntity<ApiResponse<Cursor<GetStoreReviewsResponse, UUID>>> findStoreReviews(
		@PathVariable("storeId") UUID storeId,
		@ModelAttribute GetStoreReviewCursorRequest request
	) {
		Cursor<GetStoreReviewsResponse, UUID> response = reviewService.findStoreReviews(storeId, request.toCursorRequest(), request.sortBy())
			.map(GetStoreReviewsResponse::from);

		return ResponseEntity.ok(
			ApiResponse.success(
				"리뷰 조회 성공",
				response,
				HttpStatus.OK
			));
	}

	@GetMapping("/users/me/reviews")
	public ResponseEntity<ApiResponse<Cursor<GetMyReviewsResponse, UUID>>> findMyReviews(
		@RequestHeader("X-User-Id") String strUserId,
		@ModelAttribute GetMyReviewCursorRequest request
	) {
		UUID userId = UUID.fromString(strUserId);

		Cursor<GetMyReviewsResponse, UUID> response = reviewService.findMyReviews(userId, request.toCursorRequest())
			.map(GetMyReviewsResponse::from);

		return ResponseEntity.ok(
			ApiResponse.success(
				"리뷰 조회 성공",
				response,
				HttpStatus.OK
			));
	}
}
