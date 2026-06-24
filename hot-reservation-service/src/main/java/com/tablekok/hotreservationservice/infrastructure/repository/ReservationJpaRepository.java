package com.tablekok.hotreservationservice.infrastructure.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tablekok.hotreservationservice.domain.entity.Reservation;
import com.tablekok.hotreservationservice.domain.entity.ReservationStatus;

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

}
