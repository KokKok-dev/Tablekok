package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.store_service.presentation.dto.request.CreateCategoryRequest;
import com.tablekok.store_service.presentation.dto.response.CategoryResponse;
import com.tablekok.util.PageableUtils;

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
			.path("/{categoryId}")
			.buildAndExpand(UUID.randomUUID())
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("카테고리 생성 성공", HttpStatus.CREATED));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getCategories(
		Pageable pageable
	) {
		// 모든 카테고리 조회
		pageable = PageableUtils.normalize(pageable);

		List<CategoryResponse> categories = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			categories.add(new CategoryResponse(
				UUID.randomUUID(),
				"카테고리 " + i
			));
		}

		Page<CategoryResponse> dummyPage = new PageImpl<>(
			categories,
			pageable,
			5
		);

		return ResponseEntity.ok(
			ApiResponse.success("카테고리 목록 조회 성공", dummyPage, HttpStatus.OK)
		);
	}

}
