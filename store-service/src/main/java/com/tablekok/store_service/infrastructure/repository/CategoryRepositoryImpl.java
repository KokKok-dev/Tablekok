package com.tablekok.store_service.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public void save(Category category) {
		categoryJpaRepository.save(category);
	}

	@Override
	public boolean existsByName(String name) {
		return categoryJpaRepository.existsByName(name);
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return categoryJpaRepository.findAll(pageable);
	}

	@Override
	public List<Category> findAllById(List<UUID> categoryIds) {
		return categoryJpaRepository.findAllById(categoryIds);
	}

}
