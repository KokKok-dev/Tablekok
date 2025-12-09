package com.tablekok.store_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

public interface StoreRepository {

	void save(Store store);

	boolean existsByNameAndAddress(String name, String address);

	Optional<Store> findById(UUID storeId);

}
