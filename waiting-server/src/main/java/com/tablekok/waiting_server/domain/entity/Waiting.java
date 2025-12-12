package com.tablekok.waiting_server.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.tablekok.entity.BaseEntity;
import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.domain.exception.WaitingDomainErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "p_waiting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waiting extends BaseEntity {

	@Id
	@Column(name = "waiting_queue_id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "waiting_number", nullable = false)
	private int waitingNumber; // 매장별 순차 부여 대기 번호 (101, 102, ...)

	// 고객 정보
	@Enumerated(EnumType.STRING)
	@Column(name = "customer_type", nullable = false, length = 20)
	private CustomerType customerType; // MEMBER, NON_MEMBER

	@Column(name = "member_id")
	private UUID memberId; // 회원 ID (비회원일 경우 NULL)

	@Column(name = "non_member_name", length = 50)
	private String nonMemberName;

	@Column(name = "non_member_phone", length = 20)
	private String nonMemberPhone;

	// 웨이팅 상세 정보
	@Column(name = "headcount", nullable = false)
	private int headcount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private WaitingStatus status;

	// 시간 정보 (queued_at = created_at)
	@CreatedDate
	@Column(name = "queued_at", nullable = false, updatable = false)
	private LocalDateTime queuedAt; // 웨이팅 등록 시간

	@Column(name = "called_at")
	private LocalDateTime calledAt; // 고객 호출 시간

	@Column(name = "entered_at")
	private LocalDateTime enteredAt; // 입장 처리 시간

	@Column(name = "canceled_at")
	private LocalDateTime canceledAt; // 취소/노쇼 처리 시간

	@Builder(access = AccessLevel.PRIVATE)
	private Waiting(
		UUID id, UUID storeId, int waitingNumber, CustomerType customerType, UUID memberId, String nonMemberName,
		String nonMemberPhone, int headcount, WaitingStatus status, Integer estimatedWaitMinutes) {

		this.id = id;
		this.storeId = storeId;
		this.waitingNumber = waitingNumber;
		this.customerType = customerType;
		this.headcount = headcount;
		this.status = status;

		this.memberId = memberId;
		this.nonMemberName = nonMemberName;
		this.nonMemberPhone = nonMemberPhone;

	}

	public static Waiting create(
		UUID storeId,
		int waitingNumber,
		CustomerType customerType,
		UUID memberId, // nullable
		String nonMemberName, // nullable
		String nonMemberPhone, // nullable
		int headcount) {

		validateCustomerInfo(customerType, memberId, nonMemberName, nonMemberPhone);

		return Waiting.builder()
			.id(UUID.randomUUID()) // 객체 생성시 ID 바로 발급
			.storeId(storeId)
			.waitingNumber(waitingNumber)
			.customerType(customerType)
			.memberId(memberId)
			.nonMemberName(nonMemberName)
			.nonMemberPhone(nonMemberPhone)
			.headcount(headcount)
			.status(WaitingStatus.WAITING)
			.build();
	}

	private static void validateCustomerInfo(
		CustomerType customerType, UUID memberId, String nonMemberName, String nonMemberPhone
	) {
		if (customerType == CustomerType.MEMBER && memberId == null) {
			throw new AppException(WaitingDomainErrorCode.MEMBER_ID_REQUIRED);
		}

		if (customerType == CustomerType.NON_MEMBER) {
			if (nonMemberName == null || nonMemberName.isBlank() || nonMemberPhone == null
				|| nonMemberPhone.isBlank()) {
				throw new AppException(WaitingDomainErrorCode.NON_MEMBER_INFO_REQUIRED);
			}
		}
	}

	public boolean isMember() {
		return this.customerType == CustomerType.MEMBER;
	}

	public boolean isNonMember() {
		return this.customerType == CustomerType.NON_MEMBER;
	}

	public void callCustomer() {
		if (this.getStatus() != WaitingStatus.WAITING) {
			throw new AppException(WaitingDomainErrorCode.INVALID_WAITING_STATUS);
		}

		this.status = WaitingStatus.CALLED;
		this.calledAt = LocalDateTime.now();
	}

	public void noShow() {
		this.status = WaitingStatus.NO_SHOW;
		this.canceledAt = LocalDateTime.now();
	}
}
