package com.tablekok.reservation_service.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.client.StoreClient;
import com.tablekok.reservation_service.application.client.dto.request.OwnerVerificationRequest;
import com.tablekok.reservation_service.application.client.dto.response.GetStoreReservationPolicyResponse;
import com.tablekok.reservation_service.application.dto.command.CreateReservationCommand;
import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationsForCustomerResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationsForOwnerResult;
import com.tablekok.reservation_service.application.dto.result.GetReservedTimeResult;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;
import com.tablekok.reservation_service.application.service.strategy.RoleStrategy;
import com.tablekok.reservation_service.application.service.strategy.StrategyFactory;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.service.ReservationDomainService;
import com.tablekok.reservation_service.domain.vo.StoreReservationPolicy;
import com.tablekok.reservation_service.global.annotation.DistributedLock;
import com.tablekok.util.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ReservationDomainService reservationDomainService;
	private final StoreClient storeClient;
	private final StrategyFactory strategyFactory;

	// 예약 생성(접수)
	@Transactional
	@DistributedLock(key = "'reservation:' + #command.storeId() + ':' + #command.reservationDateTime().getReservationDate()")
	public CreateReservationResult createReservation(CreateReservationCommand command) {
		validateReservationConstraints(command);

		Reservation newReservation = Reservation.create(
			command.userId(),
			command.storeId(),
			command.reservationDateTime(),
			command.headcount(),
			command.deposit()
		);

		reservationRepository.save(newReservation);
		return CreateReservationResult.of(newReservation);
	}

	// 생성 전 검증
	private void validateReservationConstraints(CreateReservationCommand command) {
		// 인기 음식점의 요청인지 확인
		List<UUID> hotStores = storeClient.getHotStores();
		reservationDomainService.validateHotStore(
			hotStores,
			command.storeId()
		);

		// 그 시간대 예약이 있는지
		reservationDomainService.validateDuplicateReservation(
			command.storeId(),
			command.reservationDateTime()
		);

		// 예약할 음식점의 예약 정책에 준수하는지
		StoreReservationPolicy policy = GetStoreReservationPolicyResponse.toVo(
			storeClient.getStoreReservationPolicy(command.storeId()));
		reservationDomainService.validateStoreReservationPolicy(
			command.headcount(),
			command.reservationDateTime(),
			policy
		);
	}

	// 특정 식당의 선택 일자의 예약 목록 조회(프론트에서 예약 가능 시간 선택 표시를 위해)
	@Transactional(readOnly = true)
	public GetReservedTimeResult getReservedTime(UUID storeId, LocalDate date) {
		List<Reservation> findReservations = reservationRepository.findByStoreIdAndReservationDateTime_ReservationDate(
			storeId, date);
		return GetReservedTimeResult.of(findReservations);
	}

	// 단건 예약 조회(리뷰에서 호출 용도)
	@Transactional(readOnly = true)
	public GetReservationResult getReservation(UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		return GetReservationResult.of(findReservation);
	}

	// 예약 인원수 변경
	@Transactional
	public void updateHeadcount(UUID userId, UUID reservationId, Integer headcount, UserRole role) {
		if (role.equals(UserRole.CUSTOMER)) {
			Reservation findReservation = reservationRepository.findByIdAndUserId(reservationId, userId);
			// 인원수 정책 검증
			StoreReservationPolicy policy = GetStoreReservationPolicyResponse.toVo(
				storeClient.getStoreReservationPolicy(findReservation.getStoreId()));
			reservationDomainService.validateHeadcount(headcount, policy);
			findReservation.updateHeadcount(headcount);
			return;
		}
		if (role.equals(UserRole.OWNER)) {
			Reservation findReservation = reservationRepository.findById(reservationId);
			findReservation.updateHeadcount(headcount);
		}
	}

	// 예약 취소
	@Transactional
	public void cancelReservation(UUID userId, UserRole userRole, UUID reservationId) {
		RoleStrategy strategy = strategyFactory.getStrategy(userRole);
		strategy.cancelReservation(userId, reservationId);
	}

	// 예약 노쇼(오너)
	@Transactional
	public void noShow(UUID userId, UUID reservationId, UserRole role) {
		Reservation findReservation = reservationRepository.findById(reservationId);

		if (role.equals(UserRole.OWNER)) {
			validateStoreOwner(userId, findReservation.getStoreId());
		}
		findReservation.noShow();
	}

	// 예약 확인(DONE, 오너)
	@Transactional
	public void done(UUID userId, UUID reservationId, UserRole role) {
		Reservation findReservation = reservationRepository.findById(reservationId);

		if (role.equals(UserRole.OWNER)) {
			validateStoreOwner(userId, findReservation.getStoreId());
		}
		findReservation.done();
	}

	// 예약 조회(고객)
	@Transactional(readOnly = true)
	public Page<GetReservationsForCustomerResult> getReservationsForCustomer(UUID userId, Pageable pageable) {
		Pageable normalizedPageable = PageableUtils.normalize(pageable);

		Page<Reservation> reservations = reservationRepository.findByUserId(userId, normalizedPageable);
		return GetReservationsForCustomerResult.toPage(reservations);
	}

	// 식당 예약 조회
	@Transactional(readOnly = true)
	public Page<GetReservationsForOwnerResult> getReservationsForOwner(
		UUID userId,
		UUID storeId,
		UserRole role,
		Pageable pageable
	) {
		if (role.equals(UserRole.OWNER)) {
			validateStoreOwner(userId, storeId);
		}
		Pageable normalizedPageable = PageableUtils.normalize(pageable);

		Page<Reservation> reservations = reservationRepository.findByStoreId(storeId, normalizedPageable);
		return GetReservationsForOwnerResult.toPage(reservations);
	}

	// 해당 예약의 음식점이 사용자 소유인지
	private void validateStoreOwner(UUID userId, UUID storeId) {
		OwnerVerificationRequest request = OwnerVerificationRequest.of(storeId, userId);
		if (!storeClient.checkStoreOwner(request)) {
			throw new AppException(ReservationErrorCode.FORBIDDEN_STORE_ACCESS);
		}
	}

}
