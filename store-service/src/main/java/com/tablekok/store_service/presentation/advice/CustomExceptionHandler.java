package com.tablekok.store_service.presentation.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.dto.ApiResponse;
import com.tablekok.exception.GlobalExceptionHandler;
import com.tablekok.store_service.application.exception.StoreErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler extends GlobalExceptionHandler {

	private final ObjectMapper objectMapper;

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(ApiResponse.error(
				StoreErrorCode.FORBIDDEN_ACCESS,
				StoreErrorCode.FORBIDDEN_ACCESS.getMessage()
			));
	}
}
