package com.tablekok.store_service.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tablekok.store_service.domain.entity.Category;

public interface CategoryRepository {
	void save(Category category);

	boolean existsByName(String name);

	Page<Category> findAll(Pageable pageable);

	Long countByIdIn(List<UUID> categoryIds);

}
