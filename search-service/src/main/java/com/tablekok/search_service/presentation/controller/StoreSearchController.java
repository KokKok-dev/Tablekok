package com.tablekok.search_service.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tablekok.cursor.dto.response.Cursor;
import com.tablekok.dto.ApiResponse;
import com.tablekok.search_service.application.service.StoreSearchService;
import com.tablekok.search_service.presentation.dto.request.SearchCategoryStoreRequest;
import com.tablekok.search_service.presentation.dto.request.StoreSearchRequest;
import com.tablekok.search_service.presentation.dto.response.SearchCategoryStoreResponse;
import com.tablekok.search_service.presentation.dto.response.SearchStoreResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class StoreSearchController {

	private final StoreSearchService storeSearchService;

	@GetMapping("/category/{categoryId}/stores")
	public ResponseEntity<ApiResponse<Cursor<SearchCategoryStoreResponse, String>>> getStoresByCategory(
		@PathVariable("categoryId") UUID categoryId,
		@ModelAttribute SearchCategoryStoreRequest request
	) {
		Cursor<SearchCategoryStoreResponse, String> response = storeSearchService
			.searchByCategory(categoryId, request.sortBy(), request.toCursorRequest())
			.map(SearchCategoryStoreResponse::from);

		return ResponseEntity.ok(
			ApiResponse.success(
				"음식점 조회 성공.",
				response,
				HttpStatus.OK
			)
		);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Cursor<SearchStoreResponse, String>>> search(
		@ModelAttribute StoreSearchRequest request
	) {
		Cursor<SearchStoreResponse, String> response = storeSearchService.search(request.toCommand())
			.map(SearchStoreResponse::from);

		return ResponseEntity.ok(
			ApiResponse.success(
				"음식점 검색 성공",
				response,
				HttpStatus.OK
			)
		);
	}

}
