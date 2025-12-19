package com.tablekok.store_service.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.store_service.domain.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {
	boolean existsByName(String name);

	long countByIdIn(List<UUID> ids);
}
