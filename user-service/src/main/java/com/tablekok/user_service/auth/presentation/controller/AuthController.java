package com.tablekok.user_service.auth.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tablekok.dto.ApiResponse;
import com.tablekok.user_service.auth.application.dto.result.LoginResult;
import com.tablekok.user_service.auth.application.dto.result.RefreshTokenResult;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.application.service.AuthApplicationService;
import com.tablekok.user_service.auth.presentation.dto.request.LoginRequest;
import com.tablekok.user_service.auth.presentation.dto.request.SignupRequest;
import com.tablekok.user_service.auth.presentation.dto.response.LoginResponse;
import com.tablekok.user_service.auth.presentation.dto.response.RefreshTokenResponse;
import com.tablekok.user_service.auth.presentation.dto.response.SignupResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthApplicationService authApplicationService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signup(
		@Valid @RequestBody SignupRequest request
	) {
		SignupResult result = authApplicationService.signup(request.toCommand());
		SignupResponse response = SignupResponse.from(result);

		return ResponseEntity.status(HttpStatus.CREATED)
			.header("Authorization", "Bearer " + result.accessToken())
			.body(ApiResponse.success("회원가입이 완료되었습니다.", response, HttpStatus.CREATED));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		LoginResult result = authApplicationService.login(request.toCommand());
		LoginResponse response = LoginResponse.from(result);

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + result.accessToken())
			.header("X-Refresh-Token", result.refreshToken())
			.body(ApiResponse.success("로그인이 완료되었습니다.", response, HttpStatus.OK));
	}
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
		@RequestHeader("X-Refresh-Token") String refreshToken
	) {
		RefreshTokenResult result = authApplicationService.refresh(refreshToken);
		RefreshTokenResponse response = RefreshTokenResponse.from(result);

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + result.accessToken())
			.body(ApiResponse.success("토큰이 재발급되었습니다.", response, HttpStatus.OK));
	}
}
