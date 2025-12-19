package com.tablekok.store_service.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.store_service.domain.entity.Store;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {
	boolean existsByNameAndAddress(String name, String address);

	// 특정 Store (excludedId)를 제외하고 검색
	// 주어진 이름(name)과 주소(address)를 기준으로 중복되는 Store 엔티티가 존재하는지 검색
	boolean existsByNameAndAddressAndIdNot(String name, String address, UUID excludedId);

	List<StoreIdOnly> findByIsHotTrue(); // Projection 사용

	boolean existsByIdAndOwnerId(UUID storeId, UUID ownerId);

	interface StoreIdOnly {
		UUID getId();
	}
}
