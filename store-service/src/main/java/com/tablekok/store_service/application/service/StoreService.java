package com.tablekok.store_service.application.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.command.CreateOperatingHourCommand;
import com.tablekok.store_service.application.dto.command.CreateReservationPolicyCommand;
import com.tablekok.store_service.application.dto.command.CreateStoreCommand;
import com.tablekok.store_service.application.dto.result.CreateStoreResult;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.entity.ReservationPolicy;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;
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

	@Transactional
	public void createReservationPolicy(UUID storeId, CreateReservationPolicyCommand command) {
		/*
		 * [A] 일관성 및 존재 여부 검증
		 */
		// 1. 실제 storeId가 있는지 확인
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new AppException(StoreErrorCode.STORE_NOT_FOUND));

		// 2. Store에 이미 ReservationPolicy가 등록되어 있는지 확인
		if (store.getReservationPolicy() != null) {
			throw new AppException(StoreErrorCode.POLICY_ALREADY_EXISTS);
		}
		// 3. Store Status가 정책 생성을 허용하는 상태인지 확인
		StoreStatus status = store.getStatus();
		if (status == StoreStatus.PENDING_APPROVAL ||
			status == StoreStatus.APPROVAL_REJECTED ||
			status == StoreStatus.DECOMMISSIONED) {
			throw new AppException(StoreErrorCode.INVALID_STORE_STATUS);
		}


		/*
		 * [B] 입력 데이터 논리 검증  (Domain Validation)
		 */
		// 1. 월별 오픈 날짜(monthlyOpenDay) 유효성 검증 (1 ~ 28일 이내)
		if (command.monthlyOpenDay() < 1 || command.monthlyOpenDay() > 28) {
			throw new AppException(StoreErrorCode.INVALID_OPEN_DAY);
		}

		// 2. 예약 가능한 간격 분 10/ 15 /20/30/60/120 입력인지 확인
		List<Integer> validIntervals = List.of(10, 15, 20, 30, 60, 120);
		if (!validIntervals.contains(command.reservationInterval())) {
			throw new AppException(StoreErrorCode.INVALID_RESERVATION_INTERVAL);
		}

		// 3. dailyReservationEndTime이 dailyReservationStartTime보다 이후 시간인지 확인
		if (!command.dailyReservationEndTime().isAfter(command.dailyReservationStartTime())) {
			throw new AppException(StoreErrorCode.INVALID_TIME_RANGE);
		}

		// 4. dailyReservationStartTime이 Store 운영시간 OpenTime이 이후 또는 같은지 확인
		validateReservationStartTime(store, command.dailyReservationStartTime());
		//  dailyReservationEndTime이 Store 운영시간 EndTime이 이전 또는 같은지 확인
		validateReservationEndTime(store, command.dailyReservationEndTime());

		// 5. dailyReservationEndTime과 dailyReservationStartTime 사이의 시간이 reservationInterval보다 충분히 긴지 확인
		Duration duration = Duration.between(command.dailyReservationStartTime(), command.dailyReservationEndTime());
		if (duration.toMinutes() < command.reservationInterval()) {
			throw new AppException(StoreErrorCode.INSUFFICIENT_TIME_SLOT);
		}

		// 6. maxHeadcount가 minHeadcount보다 크거나 같은지 확인
		if (command.maxHeadcount() < command.minHeadCount()) {
			throw new AppException(StoreErrorCode.INVALID_HEADCOUNT_RANGE);
		}

		// 7. is_deposit_required가 true인 경우 deposit_amount는 0보다 큰가
		if (command.isDepositRequired() && command.depositAmount() <= 0) {
			throw new AppException(StoreErrorCode.INVALID_DEPOSIT_AMOUNT);
		}

		/*
		 * [C] 정책 생성 및 저장
		 */
		ReservationPolicy policy = command.toEntity(store);
		store.setReservationPolicy(policy);
		store.setReservationOpenTime(policy.getOpenTime());
		storeRepository.save(store);

	}

	private void validateReservationStartTime(Store store, LocalTime policyStartTime) {
		for (OperatingHour hour : store.getOperatingHours()) {
			// 1. 휴무일은 건너뜁니다.
			if (hour.isClosed()) {
				continue;
			}

			// 2. 예약 시작 시간이 해당 요일의 실제 운영 시작 시간보다 이전인지 확인
			if (policyStartTime.isBefore(hour.getOpenTime())) {
				throw new AppException(StoreErrorCode.RESERVATION_TIME_BEFORE_OPERATING_OPEN);
			}
		}
	}

	private void validateReservationEndTime(Store store, LocalTime policyEndTime) {
		for (OperatingHour hour : store.getOperatingHours()) {
			// 1. 휴무일은 건너뜁니다.
			if (hour.isClosed()) {
				continue;
			}

			// 2. 예약 시작 시간이 해당 요일의 실제 운영 시작 시간보다 이전인지 확인
			if (policyEndTime.isAfter(hour.getCloseTime())) {
				throw new AppException(StoreErrorCode.RESERVATION_TIME_AFTER_OPERATING_CLOSE);
			}
		}
	}

}
