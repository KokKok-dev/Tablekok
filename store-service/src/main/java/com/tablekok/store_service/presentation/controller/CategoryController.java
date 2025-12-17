package com.tablekok.store_service.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tablekok.dto.ApiResponse;
import com.tablekok.store_service.application.dto.result.FindCategoryResult;
import com.tablekok.store_service.application.service.CategoryService;
import com.tablekok.store_service.presentation.dto.request.CreateCategoryRequest;
import com.tablekok.store_service.presentation.dto.response.GetCategoryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('MASTER')")
	public ResponseEntity<ApiResponse<Void>> createCategory(
		@Valid @RequestBody CreateCategoryRequest request,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		UUID categoryId = categoryService.createCategory(request.toCommand());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{categoryId}")
			.buildAndExpand(categoryId)
			.toUri();

		return ResponseEntity.created(location)
			.body(ApiResponse.success("카테고리 생성 성공", HttpStatus.CREATED));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetCategoryResponse>>> getCategories(
		Pageable pageable,
		@RequestHeader("X-User-Id") String userId,
		@RequestHeader("X-User-Role") String userRole
	) {
		Page<FindCategoryResult> categoryPage = categoryService.getCategories(pageable);
		Page<GetCategoryResponse> responsePage = categoryPage.map(GetCategoryResponse::from);
		return ResponseEntity.ok(
			ApiResponse.success("카테고리 목록 조회 성공", responsePage, HttpStatus.OK)
		);
	}

}
