package com.tablekok.store_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.param.CreateOperatingHourParam;
import com.tablekok.store_service.application.dto.param.CreateStoreParam;
import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;
import com.tablekok.store_service.domain.service.CategoryLinker;
import com.tablekok.store_service.domain.service.OperatingHourValidator;
import com.tablekok.store_service.domain.vo.OperatingHourData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final CategoryLinker categoryDomainService;
	private final OperatingHourValidator operatingHourValidator;

	@Transactional
	public CreateStoreResult createStore(CreateStoreParam param) {
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

		// OperatingHour Entity 생성 및 Store 컬렉션에 추가
		List<OperatingHour> hoursToSave = param.operatingHours().stream()
			.map(p -> {
				OperatingHourData vo = p.toVo();
				// Store를 인자로 넘겨서 OperatingHour Entity 생성
				return OperatingHour.of(store, vo);
			})
			.toList();
		store.getOperatingHours().addAll(hoursToSave);

		// store db 저장
		storeRepository.save(store);

		return CreateStoreResult.of(store, hoursToSave);
	}

}
