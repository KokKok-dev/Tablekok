package com.tablekok.store_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.store_service.application.dto.param.CreateStoreParam;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;

	@Transactional
	public UUID createStore(CreateStoreParam param) {
		// 음식점 생성
		Store store = param.toEntity();
		storeRepository.save(store);
		return store.getId();
	}
}
