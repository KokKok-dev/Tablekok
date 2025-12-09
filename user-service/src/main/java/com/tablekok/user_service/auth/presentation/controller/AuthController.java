package com.tablekok.user_service.auth.presentation.controller;

import com.tablekok.dto.ApiResponse;
import com.tablekok.user_service.auth.application.dto.command.CustomerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.OwnerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.LoginCommand;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.application.dto.result.LoginResult;
import com.tablekok.user_service.auth.application.service.AuthApplicationService;
import com.tablekok.user_service.auth.presentation.dto.request.CustomerSignupRequest;
import com.tablekok.user_service.auth.presentation.dto.request.OwnerSignupRequest;
import com.tablekok.user_service.auth.presentation.dto.request.LoginRequest;
import com.tablekok.user_service.auth.presentation.dto.response.SignupResponse;
import com.tablekok.user_service.auth.presentation.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API Controller
 *
 * gashine20 피드백 반영: Request DTO의 toCommand() 메서드 사용
 *
 * 주요 책임:
 * 1. HTTP 요청/응답 처리
 * 2. Request DTO → Command DTO 변환 (toCommand() 사용)
 * 3. Result DTO → Response DTO 변환
 * 4. HTTP 상태 코드 관리
 *
 * 팀 컨벤션: Common 모듈 ApiResponse 활용
 * 팀 컨벤션: Request/Response DTO로 계층 분리
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 로그인 관련 API")
public class AuthController {

	private final AuthApplicationService authApplicationService;

	// ========== 고객 회원가입 ==========

	@PostMapping("/signup/customer")
	@Operation(
		summary = "고객 회원가입",
		description = "이메일, 비밀번호, 이름, 휴대폰번호로 고객 회원가입을 처리합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = SignupResponse.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (이메일 중복, 유효성 검증 실패 등)"
		)
	})
	public ResponseEntity<ApiResponse<SignupResponse>> signupCustomer(
		@Valid @RequestBody CustomerSignupRequest request
	) {
		log.info("Customer signup request received for email: {}", request.email());

		// ✅ gashine20 피드백 반영: request.toCommand() 사용
		CustomerSignupCommand command = request.toCommand();

		// Service 호출
		SignupResult result = authApplicationService.signupCustomer(command);

		// Result → Response 변환
		SignupResponse response = SignupResponse.from(result);

		log.info("Customer signup completed successfully for email: {}", request.email());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("고객 회원가입이 완료되었습니다.", response, HttpStatus.CREATED));
	}

	// ========== 사장님 회원가입 ==========

	@PostMapping("/signup/owner")
	@Operation(
		summary = "사장님 회원가입",
		description = "이메일, 비밀번호, 이름, 휴대폰번호, 사업자번호로 사장님 회원가입을 처리합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = SignupResponse.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (이메일 중복, 사업자번호 오류 등)"
		)
	})
	public ResponseEntity<ApiResponse<SignupResponse>> signupOwner(
		@Valid @RequestBody OwnerSignupRequest request
	) {
		log.info("Owner signup request received for email: {} with business number", request.email());

		// ✅ gashine20 피드백 반영: request.toCommand() 사용
		OwnerSignupCommand command = request.toCommand();

		// Service 호출
		SignupResult result = authApplicationService.signupOwner(command);

		// Result → Response 변환
		SignupResponse response = SignupResponse.from(result);

		log.info("Owner signup completed successfully for email: {}", request.email());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("사장님 회원가입이 완료되었습니다.", response, HttpStatus.CREATED));
	}

	// ========== 로그인 ==========

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		description = "이메일과 비밀번호로 로그인을 처리합니다. 모든 역할(고객/사장님/관리자) 공통 사용"
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = LoginResponse.class))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "잘못된 요청"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패 (이메일 또는 비밀번호 오류)"
		)
	})
	public ResponseEntity<ApiResponse<LoginResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		log.info("Login request received for email: {}", request.email());

		// ✅ gashine20 피드백 반영: request.toCommand() 사용
		LoginCommand command = request.toCommand();

		// Service 호출
		LoginResult result = authApplicationService.login(command);

		// Result → Response 변환
		LoginResponse response = LoginResponse.from(result);

		log.info("Login completed successfully for email: {} with role: {}",
			request.email(), response.role());

		return ResponseEntity.ok(
			ApiResponse.success("로그인이 완료되었습니다.", response, HttpStatus.OK)
		);
	}
}
