package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.param.CreateOperatingHourParam;
import com.tablekok.store_service.application.dto.param.CreateStoreParam;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.OperatingHourRepository;
import com.tablekok.store_service.domain.repository.StoreRepository;
import com.tablekok.store_service.domain.service.CategoryLinker;
import com.tablekok.store_service.domain.service.OperatingHourValidator;
import com.tablekok.store_service.domain.vo.OperatingHourData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OperatingHourRepository operatingHourRepository;
	private final CategoryLinker categoryDomainService;
	private final OperatingHourValidator operatingHourValidator;

	@Transactional
	public UUID createStore(CreateStoreParam param) {
		// 음식점 중복확인
		if (storeRepository.existsByNameAndAddress(param.name(), param.address())) {
			throw new AppException(StoreErrorCode.DUPLICATE_STORE_ENTRY);
		}

		List<OperatingHourData> operatingHourDataList = param.operatingHours().stream()
			.map(CreateOperatingHourParam::toVo)
			.toList();

		// 운영시간 검증
		operatingHourValidator.validateOperatingHours(operatingHourDataList);

		// Store Entity 생성 (PENDING_APPROVAL 상태로)
		Store store = param.toEntity();

		// Category ID를 사용하여 Entity 조회 및 연결
		categoryDomainService.linkCategoriesToStore(store, param.categoryIds());

		// store db 저장
		storeRepository.save(store);

		// operatingHour db 저장
		saveOperatingHours(store, param.operatingHours());

		return store.getId();

	}

	private void saveOperatingHours(Store store, List<CreateOperatingHourParam> hourParams) {
		// Param 목록을 OperatingHour Entity 목록으로 변환
		List<OperatingHour> hoursToSave = hourParams.stream()
			.map(param -> param.toEntity(store))
			.toList();

		operatingHourRepository.saveAll(hoursToSave);

	}
}
