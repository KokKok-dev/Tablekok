package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternalStoreService {
	private final StoreRepository storeRepository;

	public List<UUID> findPopularStores() {
		return storeRepository.findHotStoreIds();
	}

	public boolean isOwner(UUID storeId, UUID ownerId) {
		return storeRepository.isOwner(storeId, ownerId);
	}

	public UUID getOwnerIdByStoreId(UUID storeId) {
		return storeRepository.findById(storeId)
			.map(Store::getOwnerId)
			.orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));
	}
}
