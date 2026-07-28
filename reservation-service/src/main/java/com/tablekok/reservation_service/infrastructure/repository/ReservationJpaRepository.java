package com.tablekok.reservation_service.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tablekok.reservation_service.domain.entity.Reservation;
import com.tablekok.reservation_service.domain.entity.ReservationStatus;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {

	@Query("""
		select count(r) > 0 from Reservation r
		 where r.storeId = :storeId
		   and r.reservationDateTime.reservationDate = :reservationDate
		   and r.reservationDateTime.reservationTime = :reservationTime
		   and r.reservationStatus not in :excludedStatuses
		   and r.deletedAt is null
		""")
	boolean existsActiveReservation(
		@Param("storeId") UUID storeId,
		@Param("reservationDate") LocalDate reservationDate,
		@Param("reservationTime") LocalTime reservationTime,
		@Param("excludedStatuses") Collection<ReservationStatus> excludedStatuses
	);

	Optional<Reservation> findByIdAndUserId(UUID reservationId, UUID userId);

	Page<Reservation> findByUserId(UUID userId, Pageable pageable);

	Page<Reservation> findByStoreId(UUID storeId, Pageable pageable);

	@Query("""
		select r from Reservation r
		 where r.storeId = :storeId
		   and r.reservationDateTime.reservationDate = :reservationDate
		   and r.reservationStatus not in :excludedStatuses
		   and r.deletedAt is null
		""")
	List<Reservation> findActiveByStoreIdAndDate(
		@Param("storeId") UUID storeId,
		@Param("reservationDate") LocalDate reservationDate,
		@Param("excludedStatuses") Collection<ReservationStatus> excludedStatuses
	);
}
