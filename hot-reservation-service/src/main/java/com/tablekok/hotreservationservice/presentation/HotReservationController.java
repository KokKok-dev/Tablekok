package com.tablekok.hotreservationservice.presentation;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.dto.auth.AuthUser;
import com.tablekok.hotreservationservice.application.dto.result.CreateReservationResult;
import com.tablekok.hotreservationservice.application.service.HotReservationService;
import com.tablekok.hotreservationservice.application.service.QueueService;
import com.tablekok.hotreservationservice.presentation.dto.request.CreateReservationRequest;
import com.tablekok.hotreservationservice.presentation.dto.response.CreateReservationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/hot-reservations")
@RequiredArgsConstructor
public class HotReservationController {
	private final HotReservationService hotReservationService;
	private final QueueService queueService;

	// SSE 연결 실시간 순서 업데이트를 받기 위해 연결 대기 순서도 리턴
	@PreAuthorize("isAuthenticated()")
	@GetMapping(value = "/queue", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect(
		@AuthenticationPrincipal AuthUser authUser
	) {
		return queueService.enterQueue(authUser.userId());
	}

	// 입장 유저 검증
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/validation")
	public ResponseEntity<ApiResponse<Void>> validateAvailableUser(
		@AuthenticationPrincipal AuthUser authUser
	) {
		queueService.validateAvailableUser(authUser.userId());

		return ResponseEntity.ok(
			ApiResponse.success("유저가 검증되었습니다.", HttpStatus.ACCEPTED));
	}

	// 예약 요청
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<ApiResponse<CreateReservationResponse>> createReservation(
		@Valid @RequestBody CreateReservationRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		// 유저 검사
		queueService.validateAvailableUser(authUser.userId());

		// 예약 진행
		CreateReservationResult result = hotReservationService.createReservation(request.toCommand(authUser.userId()));

		// 예약 요청 후 토큰, 해시테이블, emitter 삭제
		queueService.completeReservation(authUser.userId());

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{reservationId}")
			.buildAndExpand(result.reservationId())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("예약 성공",
				CreateReservationResponse.fromResult(result),
				HttpStatus.CREATED));
	}
}
