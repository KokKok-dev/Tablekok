package com.tablekok.store_service.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tablekok.store_service.application.dto.param.CreateCategoryParam;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.repository.CategoryRepository;
import com.tablekok.util.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public void createCategory(CreateCategoryParam param) {
		Category category = param.toEntity();
		categoryRepository.save(category);
	}

	public Page<Category> findAllCategories(Pageable pageable) {
		pageable = PageableUtils.normalize(pageable);
		return categoryRepository.findAll(pageable);
	}
}
