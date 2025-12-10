package com.tablekok.store_service.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tablekok.store_service.application.service.InternalStoreService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/v1/internal/stores")
@AllArgsConstructor
public class InternalStoreController {

	private final InternalStoreService internalStoreService;

	@GetMapping("/popular")
	public ResponseEntity<List<UUID>> getPopularRestaurants() {

		List<UUID> popularIds = internalStoreService.findPopularStores();
		return ResponseEntity.ok(popularIds);
	}

}
