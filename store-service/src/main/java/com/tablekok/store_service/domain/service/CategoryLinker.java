package com.tablekok.store_service.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;
import com.tablekok.store_service.domain.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryLinker {

	private final CategoryRepository categoryRepository;

	// category - store 매핑
	public void linkCategoriesToStore(Store store, List<UUID> categoryIds) {
		// 1. 카테고리 ID 유효성 검증 (존재하지 않는 ID가 있는지 확인)
		List<Category> categories = categoryRepository.findAllById(categoryIds);
		if (categories.size() != categoryIds.size()) {
			throw new AppException(StoreDomainErrorCode.INVALID_CATEGORY_ID);
		}

		// 2. Store Entity의 컬렉션에 Category Entity 추가
		for (Category category : categories) {
			store.addCategory(category);
		}

	}
}
