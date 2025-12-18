package com.tablekok.hotreservationservice.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tablekok.dto.ApiResponse;
import com.tablekok.exception.GlobalExceptionHandler;
import com.tablekok.hotreservationservice.application.exception.HotReservationErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler extends GlobalExceptionHandler {

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthorizationDeniedException() {
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(ApiResponse.error(
				HotReservationErrorCode.FORBIDDEN_ACCESS,
				HotReservationErrorCode.FORBIDDEN_ACCESS.getMessage()
			));
	}
}
