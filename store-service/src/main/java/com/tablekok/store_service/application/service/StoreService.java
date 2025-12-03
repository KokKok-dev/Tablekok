package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.param.CreateStoreParam;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.CategoryRepository;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public UUID createStore(CreateStoreParam param) {
		// 1. 음식점 중복확인
		if (storeRepository.existsByNameAndAddress(param.name(), param.address())) {
			throw new AppException(StoreErrorCode.DUPLICATE_STORE_ENTRY);
		}

		// 2. Store Entity 생성 (PENDING_APPROVAL 상태로)
		Store store = param.toEntity();

		// 3. Category ID를 사용하여 Entity 조회 및 연결
		linkCategoriesToStore(store, param.categoryIds());

		// 4. db 저장
		storeRepository.save(store);
		return store.getId();
	}

	private void linkCategoriesToStore(Store store, List<UUID> categoryIds) {
		// 1. 카테고리 ID 유효성 검증 (존재하지 않는 ID가 있는지 확인)
		List<Category> categories = categoryRepository.findAllById(categoryIds);
		if (categories.size() != categoryIds.size()) {
			throw new AppException(StoreErrorCode.INVALID_CATEGORY_ID);
		}

		// 2. Store Entity의 컬렉션에 Category Entity 추가
		for (Category category : categories) {
			store.addCategory(category);
		}

	}

}
