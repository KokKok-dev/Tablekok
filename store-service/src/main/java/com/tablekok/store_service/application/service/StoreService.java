package com.tablekok.store_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.command.CreateStoreCommand;
import com.tablekok.store_service.application.dto.command.CreateStoreReservationPolicyCommand;
import com.tablekok.store_service.application.dto.command.UpdateStoreStatusCommand;
import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.application.service.strategy.StoreStatusTransitionStrategy;
import com.tablekok.store_service.application.service.strategy.StrategyFactory;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreReservationPolicy;
import com.tablekok.store_service.domain.entity.StoreStatus;
import com.tablekok.store_service.domain.repository.StoreRepository;
import com.tablekok.store_service.domain.service.CategoryLinker;
import com.tablekok.store_service.domain.service.OperatingHourValidator;
import com.tablekok.store_service.domain.service.StoreReservationPolicyValidator;
import com.tablekok.store_service.domain.vo.StoreReservationPolicyInput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

	private final StoreRepository storeRepository;
	private final CategoryLinker categoryDomainService;
	private final OperatingHourValidator operatingHourValidator;
	private final StoreReservationPolicyValidator storeReservationPolicyValidator;
	private final StrategyFactory strategyFactory;

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
			.map(createOperatingHourCommand -> createOperatingHourCommand.toEntity(store))
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

	@Transactional
	public void createStoreReservationPolicy(UUID storeId, CreateStoreReservationPolicyCommand command) {
		/*  [A] 일관성 및 존재 여부 검증 */
		// 1. 실제 storeId가 있는지 확인
		Store store = findStore(storeId);

		// 2. Store에 이미 ReservationPolicy가 등록되어 있는지 확인
		if (store.getStoreReservationPolicy() != null) {
			throw new AppException(StoreErrorCode.POLICY_ALREADY_EXISTS);
		}
		// 3. Store Status가 정책 생성을 허용하는 상태인지 확인
		store.validatePolicyCreationAllowed();


		/* [B] 입력 데이터 논리 검증  (Domain Validation) */
		StoreReservationPolicyInput input = command.toVo();
		storeReservationPolicyValidator.validate(input, store);


		/* [C] 정책 생성 및 저장 */
		StoreReservationPolicy policy = command.toEntity(store);
		store.setStoreReservationPolicy(policy);
		store.setReservationOpenTime(policy.getOpenTime());
		storeRepository.save(store);

	}

	@Transactional
	public void updateStatus(UserRole userRole, UUID storeId, UpdateStoreStatusCommand command) {
		Store store = findStore(storeId);
		StoreStatus newStatus = StoreStatus.valueOf(command.storeStatus());

		StoreStatusTransitionStrategy strategy = strategyFactory.getStrategy(userRole);
		strategy.changeStatus(store, newStatus);
	}

	private Store findStore(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));
	}
}
