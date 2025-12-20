package com.tablekok.store_service.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tablekok.store_service.application.dto.event.StoreEvent;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreEventMapper {

	private final CategoryRepository categoryRepository;

	// 카테고리 이름 List로 추출 후 StoreEvent 생성
	public StoreEvent createEvent(Store store, String operationType) {
		List<Category> categories = categoryRepository.findAllByIdIn(store.getCategoryIds());
		List<String> categoryNames = categories.stream()
			.map(Category::getName)
			.toList();
		return StoreEvent.of(store, operationType, categoryNames);
	}
}
