package com.tablekok.user_service.user.presentation.controller;

import com.tablekok.dto.ApiResponse;
import com.tablekok.dto.auth.AuthUser;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;
import com.tablekok.user_service.user.application.dto.result.UpdateProfileResult;
import com.tablekok.user_service.user.application.dto.result.UserDetailResult;
import com.tablekok.user_service.user.application.dto.result.UserListResult;
import com.tablekok.user_service.user.application.service.UserApplicationService;
import com.tablekok.user_service.user.presentation.dto.request.ChangePasswordRequest;
import com.tablekok.user_service.user.presentation.dto.request.UpdateProfileRequest;
import com.tablekok.user_service.user.presentation.dto.response.ChangePasswordResponse;
import com.tablekok.user_service.user.presentation.dto.response.ProfileResponse;
import com.tablekok.user_service.user.presentation.dto.response.UpdateProfileResponse;
import com.tablekok.user_service.user.presentation.dto.response.UserDetailResponse;
import com.tablekok.user_service.user.presentation.dto.response.UserListResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserController {

	private final UserApplicationService userApplicationService;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
		@AuthenticationPrincipal AuthUser authUser
	) {
		UUID userId = UUID.fromString(authUser.userId());
		String role = authUser.role();

		ProfileResult result = userApplicationService.getProfile(userId, role);
		ProfileResponse response = ProfileResponse.from(result);

		return ResponseEntity.ok()
			.body(ApiResponse.success("내 정보 조회가 완료되었습니다.", response, HttpStatus.OK));
	}

	@PatchMapping("/profile")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER')")
	public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateProfile(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody UpdateProfileRequest request
	) {
		UUID userId = UUID.fromString(authUser.userId());
		String role = authUser.role();

		UpdateProfileResult result = userApplicationService.updateProfile(userId, role, request.toCommand());

		UpdateProfileResponse response = UpdateProfileResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("정보가 성공적으로 수정되었습니다.", response, HttpStatus.OK));
	}

	@PatchMapping("/password")
	public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody ChangePasswordRequest request
	) {
		UUID userId = UUID.fromString(authUser.userId());

		userApplicationService.changePassword(userId, request.toCommand());

		ChangePasswordResponse response = ChangePasswordResponse.success();
		return ResponseEntity.ok()
			.body(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다.", response, HttpStatus.OK));
	}

	@GetMapping
	@PreAuthorize("hasRole('MASTER')")
	public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
		@AuthenticationPrincipal AuthUser authUser,
		Pageable pageable
	) {
		String role = authUser.role();

		UserListResult result = userApplicationService.getAllUsers(role, pageable);

		UserListResponse response = UserListResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("회원 목록을 조회했습니다.", response, HttpStatus.OK));
	}

	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('MASTER')")
	public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable("userId") UUID targetUserId
	) {
		String role = authUser.role();

		UserDetailResult result = userApplicationService.getUserDetail(role, targetUserId);

		UserDetailResponse response = UserDetailResponse.from(result);
		return ResponseEntity.ok()
			.body(ApiResponse.success("회원 정보를 조회했습니다.", response, HttpStatus.OK));
	}
}
