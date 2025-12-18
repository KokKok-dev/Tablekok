package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.result.StoreWaitingInternalResult;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternalStoreService {
	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public List<UUID> findPopularStores() {
		return storeRepository.findHotStoreIds();
	}

	@Transactional(readOnly = true)
	public boolean isOwner(UUID storeId, UUID ownerId) {
		return storeRepository.isOwner(storeId, ownerId);
	}

	@Transactional(readOnly = true)
	public StoreWaitingInternalResult getStoreDetailsForWaiting(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));

		// 1. 현재 요일 구하기 (java.time.LocalDate 활용)
		java.time.DayOfWeek today = java.time.LocalDate.now().getDayOfWeek();

		// 2. 해당 요일의 운영시간 찾기
		OperatingHour todayHour = store.getOperatingHours().stream()
			.filter(oh -> oh.getDayOfWeek().equals(today))
			.findFirst()
			.orElseThrow(() -> new AppException(StoreErrorCode.OPERATING_HOUR_NOT_FOUND));

		// 3. 오늘 휴무인지 체크
		if (todayHour.isClosed()) {
			throw new AppException(StoreErrorCode.STORE_CLOSED_TODAY);
		}

		return StoreWaitingInternalResult.from(store, todayHour);
	}

}
