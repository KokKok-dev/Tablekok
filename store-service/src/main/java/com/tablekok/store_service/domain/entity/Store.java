package com.tablekok.store_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.Comment;

import com.tablekok.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "owner_id", nullable = false)
	@Comment("유저 ID")
	private UUID ownerId;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "phone_number", length = 20)
	private String phoneNumber;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@Column(name = "latitude", precision = 10, scale = 7)
	private BigDecimal latitude; // 위도, 정밀한 계산을 위해 BigDecimal 사용 권장

	@Column(name = "longitude", precision = 10, scale = 7)
	private BigDecimal longitude; // 경도, 정밀한 계산을 위해 BigDecimal 사용 권장

	@Column(name = "description", columnDefinition = "TEXT") // 긴 문자열을 위해 TEXT 타입 지정
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private StoreStatus status;

	@Column(name = "total_capacity")
	private Integer totalCapacity; // DB 스키마에 따라 int 또는 Integer 사용

	@Column(name = "turnover_rate_minutes")
	private Integer turnoverRateMinutes;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "reservation_open_time")
	private LocalTime reservationOpenTime;

	@Column(name = "waiting_open_time")
	private LocalTime waitingOpenTime;

	@Builder(access = AccessLevel.PRIVATE)
	private Store(
		UUID ownerId, String name, String phoneNumber, String address,
		BigDecimal latitude, BigDecimal longitude, String description,
		StoreStatus status, Integer totalCapacity, Integer turnoverRateMinutes,
		String imageUrl, LocalTime reservationOpenTime, LocalTime waitingOpenTime
	) {
		this.ownerId = ownerId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		// 초기 생성 시 기본 상태를 지정하거나, 필수로 받은 status를 사용합니다.
		this.status = status;
		this.totalCapacity = totalCapacity;
		this.turnoverRateMinutes = turnoverRateMinutes;
		this.imageUrl = imageUrl;
		this.reservationOpenTime = reservationOpenTime;
		this.waitingOpenTime = waitingOpenTime;
	}

	public static Store of(
		UUID ownerId, String name, String phoneNumber, String address,
		BigDecimal latitude, BigDecimal longitude, String description,
		Integer totalCapacity, Integer turnoverRateMinutes,
		String imageUrl
	) {
		return Store.builder()
			.ownerId(ownerId)
			.name(name)
			.phoneNumber(phoneNumber)
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.description(description)
			.status(StoreStatus.PENDING_APPROVAL)
			.totalCapacity(totalCapacity)
			.turnoverRateMinutes(turnoverRateMinutes)
			.imageUrl(imageUrl)
			.build();
	}

}
