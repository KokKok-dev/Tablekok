package com.tablekok.user_service.user.presentation.controller;

import com.tablekok.dto.ApiResponse;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;
import com.tablekok.user_service.user.application.dto.result.UserDetailResult;
import com.tablekok.user_service.user.application.dto.result.UserListResult;
import com.tablekok.user_service.user.application.service.UserApplicationService;
import com.tablekok.user_service.user.presentation.dto.response.ProfileResponse;
import com.tablekok.user_service.user.presentation.dto.response.UserDetailResponse;
import com.tablekok.user_service.user.presentation.dto.response.UserListResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserApplicationService userApplicationService;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
		@RequestHeader("X-User-Id") String userIdStr,
		@RequestHeader("X-User-Role") String role
	) {
		UUID userId = UUID.fromString(userIdStr);

		ProfileResult result = userApplicationService.getProfile(userId, role);
		ProfileResponse response = ProfileResponse.from(result);

		return ResponseEntity.ok()
			.body(ApiResponse.success("내 정보 조회가 완료되었습니다.", response, HttpStatus.OK));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
		@RequestHeader("X-User-Id") String userIdStr,
		@RequestHeader("X-User-Role") String role,
		Pageable pageable
	) {
		UserListResult result = userApplicationService.getAllUsers(role, pageable);

		UserListResponse response = UserListResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("회원 목록을 조회했습니다.", response, HttpStatus.OK));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
		@RequestHeader("X-User-Id") String requestUserIdStr,
		@RequestHeader("X-User-Role") String role,
		@PathVariable("userId") UUID targetUserId
	) {
		UserDetailResult result = userApplicationService.getUserDetail(role, targetUserId);

		UserDetailResponse response = UserDetailResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("회원 정보를 조회했습니다.", response, HttpStatus.OK));
	}
}
