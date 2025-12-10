package com.tablekok.store_service.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.tablekok.store_service.domain.entity.Store;

public interface StoreRepository {

	void save(Store store);

	boolean existsByNameAndAddress(String name, String address);

	Optional<Store> findById(UUID storeId);

	boolean existsByNameAndAddressAndIdNot(String name, String address, UUID excludedId);

	List<UUID> findHotStoreIds();

	boolean isOwner(UUID storeId, UUID ownerId);
}
