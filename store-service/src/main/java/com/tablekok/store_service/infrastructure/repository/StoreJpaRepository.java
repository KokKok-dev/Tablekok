package com.tablekok.store_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.store_service.domain.entity.Store;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {
	boolean existsByNameAndAddress(String name, String address);

}
