package com.tablekok.reservation_service.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.reservation_service.application.client.SearchClient;
import com.tablekok.reservation_service.application.client.dto.response.GetReservationPolicyResponse;
import com.tablekok.reservation_service.application.dto.param.CreateReservationParam;
import com.tablekok.reservation_service.application.dto.result.CreateReservationResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationsForCustomerResult;
import com.tablekok.reservation_service.application.dto.result.GetReservationsForOwnerResult;
import com.tablekok.reservation_service.application.exception.ReservationErrorCode;
import com.tablekok.reservation_service.application.service.strategy.RoleStrategy;
import com.tablekok.reservation_service.application.service.strategy.StrategyFactory;
import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.repository.ReservationRepository;
import com.tablekok.reservation_service.domain.service.ReservationDomainService;
import com.tablekok.reservation_service.domain.vo.ReservationPolicy;
import com.tablekok.util.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ReservationDomainService reservationDomainService;
	private final SearchClient searchClient;
	private final StrategyFactory strategyFactory;

	// 예약 생성(접수)
	@Transactional
	public CreateReservationResult createReservation(CreateReservationParam param) {
		Reservation newReservation = param.toEntity();

		// 인기 음식점의 요청인지 확인
		validateHotStore(newReservation);

		// 과거 시간을 예약했는지
		newReservation.validateNotPast();

		// 그 시간대 예약이 있는지
		reservationDomainService.checkDuplicateReservation(newReservation);

		// 예약할 음식점의 예약 정책에 준수하는지 		TODO 내부호출 구현 후 테스트
		// validateReservationPolicy(newReservation);

		// 저장
		reservationRepository.save(newReservation);

		return CreateReservationResult.of(newReservation);
	}

	// 인기 음식점의 요청인지 확인
	private void validateHotStore(Reservation reservation) {
		List<UUID> hotStores = searchClient.getHotStores();

		reservation.validateHotStore(hotStores);
	}

	// 예약할 음식점의 예약 정책에 준수하는지
	private void validateReservationPolicy(Reservation reservation) {
		ReservationPolicy policy = GetReservationPolicyResponse.toVo(
			searchClient.getReservationPolicy(reservation.getStoreId()));

		reservationDomainService.validateReservationPolicy(reservation, policy);
	}

	// 단건 예약 조회(리뷰에서 호출 용도)
	public GetReservationResult getReservation(UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		return GetReservationResult.of(findReservation);
	}

	// 예약 인원수 변경
	@Transactional
	public void updateHeadcount(UUID userId, UUID reservationId, Integer headcount) {
		Reservation findReservation = reservationRepository.findByIdAndUserId(reservationId, userId);

		// 인원수 정책 검증 TODO 내부호출 구현 후 테스트
		ReservationPolicy policy = GetReservationPolicyResponse.toVo(
			searchClient.getReservationPolicy(findReservation.getStoreId()));
		reservationDomainService.checkHeadcount(headcount, policy);

		findReservation.updateHeadcount(headcount);
	}

	// 예약 취소
	@Transactional
	public void cancelReservation(UUID userId, String userRole, UUID reservationId) {
		RoleStrategy strategy = strategyFactory.getStrategy(userRole); //TODO 유저 구현 후 Role값으로
		strategy.cancelReservation(userId, reservationId);
	}

	// 예약 노쇼(오너)
	@Transactional
	public void noShow(UUID userId, UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		// 해당 예약의 음식점이 사용자 소유인지
		validateStoreOwner(userId, findReservation.getStoreId());
		findReservation.noShow();
	}

	// 예약 확인(DONE, 오너)
	@Transactional
	public void done(UUID userId, UUID reservationId) {
		Reservation findReservation = reservationRepository.findById(reservationId);
		// 해당 예약의 음식점이 사용자 소유인지
		validateStoreOwner(userId, findReservation.getStoreId());
		findReservation.done();
	}

	// 예약 조회(고객)
	@Transactional(readOnly = true)
	public Page<GetReservationsForCustomerResult> getReservationsForCustomer(UUID userId, Pageable pageable) {
		Pageable normalizedPageable = PageableUtils.normalize(pageable);

		Page<Reservation> reservations = reservationRepository.findByUserId(userId, normalizedPageable);
		return GetReservationsForCustomerResult.toPage(reservations);
	}

	// 예약 조회(오너)
	@Transactional(readOnly = true)
	public Page<GetReservationsForOwnerResult> getReservationsForOwner(UUID userId, UUID storeId, Pageable pageable) {
		Pageable normalizedPageable = PageableUtils.normalize(pageable);
		validateStoreOwner(userId, storeId);

		Page<Reservation> reservations = reservationRepository.findByStoreId(storeId, normalizedPageable);
		return GetReservationsForOwnerResult.toPage(reservations);
	}

	// 해당 예약의 음식점이 사용자 소유인지
	private void validateStoreOwner(UUID userId, UUID storeId) {
		if (!searchClient.checkStoreOwner(userId, storeId)) {
			throw new AppException(ReservationErrorCode.FORBIDDEN_STORE_ACCESS);
		}
	}

}
