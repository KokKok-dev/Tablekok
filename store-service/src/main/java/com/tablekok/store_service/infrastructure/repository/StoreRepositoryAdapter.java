package com.tablekok.store_service.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepository {

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
	public Optional<Store> findById(UUID storeId) {
		return storeJpaRepository.findById(storeId);
	}

	@Override
	public boolean existsByNameAndAddressAndIdNot(String name, String address, UUID excludedId) {
		return storeJpaRepository.existsByNameAndAddressAndIdNot(name, address, excludedId);
	}

	@Override
	public List<UUID> findHotStoreIds() {
		List<StoreJpaRepository.StoreIdOnly> projections = storeJpaRepository.findByIsHotTrue();
		return projections.stream()
			.map(StoreJpaRepository.StoreIdOnly::getId)
			.collect(Collectors.toList());
	}

}
