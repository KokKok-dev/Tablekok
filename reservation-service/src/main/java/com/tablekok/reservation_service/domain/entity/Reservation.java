package com.tablekok.reservation_service.domain.entity;

import java.util.UUID;

import com.tablekok.entity.BaseEntity;
import com.tablekok.reservation_service.domain.vo.ReservationDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_reservation")
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "reservation_id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "user_id", columnDefinition = "uuid", nullable = false)
	private UUID userId;

	@Column(name = "store_id", columnDefinition = "uuid", nullable = false)
	private UUID storeId;

	@Column(name = "reservation_number", nullable = false)
	private String reservationNumber;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "reservationDate", column = @Column(name = "reservation_date", nullable = false)),
		@AttributeOverride(name = "reservationTime", column = @Column(name = "reservation_time", nullable = false))
	})
	private ReservationDateTime reservationDateTime;

	@Column(name = "headcount", nullable = false)
	private Integer headcount;

	@Column(name = "deposit")
	private Integer deposit;

	@Enumerated(EnumType.STRING)
	private ReservationStatus reservationStatus;

	@Builder(access = AccessLevel.PRIVATE)
	private Reservation(
		UUID userId, UUID storeId, String reservationNumber, ReservationDateTime reservationDateTime, Integer headcount,
		Integer deposit, ReservationStatus reservationStatus) {
		this.userId = userId;
		this.storeId = storeId;
		this.reservationNumber = reservationNumber;
		this.reservationDateTime = reservationDateTime;
		this.headcount = headcount;
		this.deposit = deposit;
		this.reservationStatus = reservationStatus;
	}

	public static Reservation create(
		UUID userId, UUID storeId, ReservationDateTime reservationDateTime, Integer headcount, Integer deposit) {

		// 예약 번호 부여
		String reservationNumber = "RSV-" + System.currentTimeMillis();

		// 예약금 여부에 따라 상태 저장
		ReservationStatus reservationStatus =
			hasDeposit(deposit) ? ReservationStatus.PENDING : ReservationStatus.RESERVED;

		return Reservation.builder()
			.userId(userId)
			.storeId(storeId)
			.reservationNumber(reservationNumber)
			.reservationDateTime(reservationDateTime)
			.headcount(headcount)
			.deposit(deposit)
			.reservationStatus(reservationStatus)
			.build();
	}

	// 인원수 변경
	public void updateHeadcount(Integer headcount) {
		this.headcount = headcount;
	}

	// 예약 취소(고객)
	public void cancel() {
		this.reservationStatus = ReservationStatus.CANCELED;
	}

	// 예약 거절(오너)
	public void reject() {
		this.reservationStatus = ReservationStatus.REJECT;
	}

	// 예약 노쇼(오너)
	public void noShow() {
		this.reservationStatus = ReservationStatus.NOSHOW;
	}

	// 예약 확인(DONE, 오너)
	public void done() {
		this.reservationStatus = ReservationStatus.DONE;
	}

	// 예약금 지불 여부. 예약금이 null이 아니고 0보다 클 때 true -> 예약금을 지불해야 하는 예약
	private static boolean hasDeposit(Integer deposit) {
		return deposit != null && deposit > 0;
	}

}
