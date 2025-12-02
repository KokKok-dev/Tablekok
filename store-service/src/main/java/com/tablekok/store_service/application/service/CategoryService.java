package com.tablekok.store_service.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.param.CreateCategoryParam;
import com.tablekok.store_service.application.dto.result.FindCategoryResult;
import com.tablekok.store_service.application.exception.CategoryErrorCode;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.repository.CategoryRepository;
import com.tablekok.util.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional
	public Category createCategory(CreateCategoryParam param) {
		validateCategoryNameDuplicate(param);

		Category category = param.toEntity();
		return categoryRepository.save(category);
	}

	@Transactional(readOnly = true)
	public Page<FindCategoryResult> getCategories(Pageable pageable) {
		pageable = PageableUtils.normalize(pageable);
		Page<Category> categoryPage = categoryRepository.findAll(pageable);
		return categoryPage.map(FindCategoryResult::from);
	}

	private void validateCategoryNameDuplicate(CreateCategoryParam param) {
		if (categoryRepository.existsByName(param.name())) {
			throw new AppException(CategoryErrorCode.DUPLICATE_CATEGORY_NAME);
		}
	}
}
