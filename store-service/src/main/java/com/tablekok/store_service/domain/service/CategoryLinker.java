package com.tablekok.store_service.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.exception.StoreDomainErrorCode;
import com.tablekok.store_service.domain.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryLinker {

	private final CategoryRepository categoryRepository;

	public void linkCategoriesToStore(Store store, List<UUID> categoryIds) {
		// 카테고리 ID 유효성 검증 (존재하지 않는 ID가 있는지 확인)
		long existingCategoryCount = categoryRepository.countByIdIn(categoryIds);

		if (existingCategoryCount != categoryIds.size()) {
			throw new AppException(StoreDomainErrorCode.INVALID_CATEGORY_ID);
		}

		// Store Entity의 컬렉션에 CategoryIds 추가
		store.updateCategoryIds(categoryIds);

	}
}
