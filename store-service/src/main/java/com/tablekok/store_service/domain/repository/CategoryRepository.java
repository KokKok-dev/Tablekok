package com.tablekok.store_service.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tablekok.store_service.domain.entity.Category;

public interface CategoryRepository {
	Category save(Category category);

	boolean existsByName(String name);

	Page<Category> findAll(Pageable pageable);
}
