package com.tablekok.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.tablekok.dto.ApiResponse;
import com.tablekok.review_service.application.client.dto.UpdateReviewStats;

@FeignClient(name = "search-service"/**, url = "${lb.dns.search}"*/)
public interface SearchFeignClient {

	@PatchMapping("/v1/search/stores/{storeId}/stats")
	ResponseEntity<ApiResponse<Void>> updateReviewStats(
		@PathVariable("storeId")UUID storeId,
		@RequestBody UpdateReviewStats stats
	);
}
