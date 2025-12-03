package com.tablekok.store_service.domain.repository;

import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

public interface StoreRepository {

	void save(Store store);

	boolean existsByNameAndAddress(String name, String address);

	Store getReferenceById(UUID storeId);
}
