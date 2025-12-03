package com.tablekok.store_service.application.service;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.param.CreateOperatingHourParam;
import com.tablekok.store_service.application.dto.param.CreateStoreParam;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.Category;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.CategoryRepository;
import com.tablekok.store_service.domain.repository.OperatingHourRepository;
import com.tablekok.store_service.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final CategoryRepository categoryRepository;
	private final OperatingHourRepository operatingHourRepository;

	@Transactional
	public UUID createStore(CreateStoreParam param) {
		// 음식점 중복확인
		if (storeRepository.existsByNameAndAddress(param.name(), param.address())) {
			throw new AppException(StoreErrorCode.DUPLICATE_STORE_ENTRY);
		}

		// 운영시간 검증
		validateOperatingHours(param.operatingHours());

		// Store Entity 생성 (PENDING_APPROVAL 상태로)
		Store store = param.toEntity();

		// Category ID를 사용하여 Entity 조회 및 연결
		linkCategoriesToStore(store, param.categoryIds());

		// store db 저장
		storeRepository.save(store);

		// operatingHour db 저장
		saveOperatingHours(store, param.operatingHours());

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

	private void validateOperatingHours(List<CreateOperatingHourParam> hours) {
		Set<DayOfWeek> days = new HashSet<>();
		for (CreateOperatingHourParam hour : hours) {

			// 1. isClosed가 true일 경우 시간 필드는 반드시 null이어야 합니다.
			if (hour.isClosed()) {
				if (hour.openTime() != null || hour.closeTime() != null) {
					throw new AppException(StoreErrorCode.INVALID_CLOSED_TIME);
				}
			}
			// 2. isClosed가 false일 경우 시간 필드는 반드시 존재해야 합니다.
			else {
				if (hour.openTime() == null || hour.closeTime() == null) {
					throw new AppException(StoreErrorCode.MISSING_OPERATING_TIME);
				}
				// 3. 시간 순서 검증
				if (hour.openTime().isAfter(hour.closeTime())) {
					throw new AppException(StoreErrorCode.INVALID_TIME_RANGE);
				}
			}

			// 4. 요일 중복 검사
			if (!days.add(hour.dayOfWeek())) {
				throw new AppException(StoreErrorCode.DUPLICATE_OPERATING_DAY);
			}

		}
		if (days.size() != 7) {
			// 7개 요일 중 하나라도 누락되었다면 예외 발생
			throw new AppException(StoreErrorCode.MISSING_ALL_OPERATING_DAYS);
		}
	}

	private void saveOperatingHours(Store store, List<CreateOperatingHourParam> hourParams) {
		// Param 목록을 OperatingHour Entity 목록으로 변환
		List<OperatingHour> hoursToSave = hourParams.stream()
			.map(param -> param.toEntity(store))
			.toList();

		operatingHourRepository.saveAll(hoursToSave);

	}
}
