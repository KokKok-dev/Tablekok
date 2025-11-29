package com.tablekok.store_service.presentation.controller;

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
import com.tablekok.store_service.presentation.dto.request.CreateCategoryRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/categories")
public class CategoryControllerV1 {

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createCategory(
		@Valid @RequestBody CreateCategoryRequest requestDto
	) {
		// category 생성
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("")
			.buildAndExpand(UUID.randomUUID())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("카테고리 생성이 완료되었습니다.", HttpStatus.CREATED));
	}

}
