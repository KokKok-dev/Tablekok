package com.tablekok.store_service.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public Category save(Category category) {
		return categoryJpaRepository.save(category);
	}
}
