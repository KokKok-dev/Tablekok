package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternalStoreService {
	private final StoreRepository storeRepository;

	public List<UUID> findPopularStores() {
		return storeRepository.findHotStoreIds();
	}
}
