package com.tablekok.store_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.command.CreateOperatingHourCommand;
import com.tablekok.store_service.application.dto.command.CreateStoreCommand;
import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.repository.StoreRepository;
import com.tablekok.store_service.domain.service.CategoryLinker;
import com.tablekok.store_service.domain.service.OperatingHourValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final CategoryLinker categoryDomainService;
	private final OperatingHourValidator operatingHourValidator;

	@Transactional
	public CreateStoreResult createStore(CreateStoreCommand command) {
		// 음식점 중복확인
		if (storeRepository.existsByNameAndAddress(command.name(), command.address())) {
			throw new AppException(StoreErrorCode.DUPLICATE_STORE_ENTRY);
		}

		// Store Entity 생성 (PENDING_APPROVAL 상태로)
		Store store = command.toEntity();

		// OperatingHourCommand -> OperatingHour Entity 생성
		List<OperatingHour> hoursToSave = command.operatingHours().stream()
			.map(CreateOperatingHourCommand::toEntity)
			.toList();

		// 운영시간 검증
		operatingHourValidator.validateOperatingHours(hoursToSave);

		// Category ID를 사용하여 Entity 조회 및 연결
		categoryDomainService.linkCategoriesToStore(store, command.categoryIds());

		// OperatingHour Entity를 Store 컬렉션에 추가
		store.getOperatingHours().addAll(hoursToSave);

		storeRepository.save(store);

		return CreateStoreResult.of(store, hoursToSave);
	}

}
