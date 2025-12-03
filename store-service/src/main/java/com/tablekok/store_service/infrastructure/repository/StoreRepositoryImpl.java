package com.tablekok.store_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

	private final StoreJpaRepository storeJpaRepository;

	@Override
	public void save(Store store) {
		storeJpaRepository.save(store);
	}

	@Override
	public boolean existsByNameAndAddress(String name, String address) {
		return storeJpaRepository.existsByNameAndAddress(name, address);
	}

	@Override
	public Store getReferenceById(UUID storeId) {
		return storeJpaRepository.getReferenceById(storeId);
	}
}
