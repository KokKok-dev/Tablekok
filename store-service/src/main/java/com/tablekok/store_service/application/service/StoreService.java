package com.tablekok.store_service.application.service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.command.CreateOperatingHourCommand;
import com.tablekok.store_service.application.dto.command.CreateStoreCommand;
import com.tablekok.store_service.application.dto.command.CreateStoreReservationPolicyCommand;
import com.tablekok.store_service.application.dto.command.UpdateStoreCommand;
import com.tablekok.store_service.application.dto.command.UpdateStoreReservationPolicyCommand;
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
		checkDuplicateStore(command.name(), command.address());

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
	public void updateStore(UpdateStoreCommand command) {
		// storeId로 Store 찾기
		Store store = findStore(command.storeId());

		// Store Status가 정보 수정 허용하는 상태인지 확인
		store.validateIsUpdatable();

		// 변경하려고 하는 음식점 정보 중복되는지 확인
		checkDuplicateStore(command.name(), command.address());

		// Store 주인이 ownerId 맞는지 확인
		// TODO : checkOwnership(store, command.ownerId());
		checkOwnership(store, store.getOwnerId());

		// 음식점 정보 수정
		store.updateInfo(
			command.name(),
			command.phoneNumber(),
			command.address(),
			command.description(),
			command.totalCapacity(),
			command.turnoverRateMinutes(),
			command.imageUrl()
		);

		// OperatingHours 수정이 일어났다면 정보 수정
		if (command.operatingHours() != null && !command.operatingHours().isEmpty()) {
			// 1. 요청 데이터를 Map으로 변환: 비교 효율성 증대 (Key: DayOfWeek)
			Map<DayOfWeek, CreateOperatingHourCommand> newHoursMap = command.operatingHours().stream()
				.collect(Collectors.toMap(CreateOperatingHourCommand::dayOfWeek, Function.identity()));

			// operatingHour 수정
			updateAllOperatingHours(store, newHoursMap);
		}
	}

	@Transactional
	public void updateStatus(UserRole userRole, UUID storeId, UpdateStoreStatusCommand command) {
		Store store = findStore(storeId);
		StoreStatus newStatus = StoreStatus.valueOf(command.storeStatus());

		StoreStatusTransitionStrategy strategy = strategyFactory.getStrategy(userRole);
		strategy.changeStatus(store, newStatus);
	}

	@Transactional
	public void deleteStore(UUID storeId, UUID deleterId) {
		Store store = findStore(storeId);

		store.changeStatus(StoreStatus.DECOMMISSIONED);
		store.softDelete(deleterId);
	}


	/* ========= ========= 예약 정책 service ========= ========= */

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
		store.validateIsUpdatable();


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
	public void updateStoreReservationPolicy(UpdateStoreReservationPolicyCommand command) {
		Store store = findStore(command.storeId());

		// Store 주인이 ownerId 맞는지 확인
		// TODO : checkOwnership(store, command.ownerId());
		checkOwnership(store, store.getOwnerId());

		// 예약 정책 찾기
		StoreReservationPolicy policy = findPolicy(store);

		// store 수정가능한 상태인지 확인
		store.validateIsUpdatable();

		// 날짜 예약 입력 검증
		StoreReservationPolicyInput input = command.toVo();
		storeReservationPolicyValidator.validate(input, store);

		// 예약 오픈시간 변경된게 있으면 store 테이블에도 업데이트
		if (!policy.getOpenTime().equals(command.openTime())) {
			store.setReservationOpenTime(command.openTime());
		}

		// policy 정보 업데이트
		policy.updatePolicyInfo(
			command.monthlyOpenDay(),
			command.openTime(),
			command.reservationInterval(),
			command.dailyReservationStartTime(),
			command.dailyReservationEndTime(),
			command.minHeadCount(),
			command.maxHeadcount(),
			command.isDepositRequired(),
			command.depositAmount(),
			command.isActive()
		);

	}

	private Store findStore(UUID storeId) {
		return storeRepository.findById(storeId)
			.orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));
	}

	private void checkDuplicateStore(String name, String address) {
		if (storeRepository.existsByNameAndAddress(name, address)) {
			throw new AppException(StoreErrorCode.DUPLICATE_STORE_ENTRY);
		}
	}

	private void checkOwnership(Store store, UUID userId) {
		if (!store.getOwnerId().equals(userId)) {
			throw new AppException(StoreErrorCode.FORBIDDEN_ACCESS); // 접근 권한 없음 예외 발생
		}
	}

	private StoreReservationPolicy findPolicy(Store store) {
		StoreReservationPolicy policy = store.getStoreReservationPolicy();
		if (policy == null) {
			throw new AppException(StoreErrorCode.POLICY_NOT_FOUND);
		}
		return policy;
	}

	private void updateAllOperatingHours(
		Store store,
		Map<DayOfWeek, CreateOperatingHourCommand> newHoursMap
	) {
		Map<DayOfWeek, OperatingHour> existingHoursMap = store.getOperatingHours().stream()
			.collect(Collectors.toMap(OperatingHour::getDayOfWeek, Function.identity()));

		// 요청 Map 을 순회하며 기존 엔티티를 찾아 업데이트
		newHoursMap.forEach(((dayOfWeek, newCommand) -> {
			OperatingHour existingHour = existingHoursMap.get(dayOfWeek);

			if (existingHour == null) {
				throw new AppException(StoreErrorCode.OPERATING_HOUR_MISSING);
			}

			existingHour.updateInfo(
				newCommand.openTime(),
				newCommand.closeTime(),
				newCommand.isClosed()
			);

			existingHour.validate();
		}));
	}
}
