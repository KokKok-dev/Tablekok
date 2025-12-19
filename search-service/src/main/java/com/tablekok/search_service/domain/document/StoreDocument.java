package com.tablekok.search_service.domain.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.tablekok.search_service.domain.dto.StoreUpdateCommand;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "stores")
public class StoreDocument extends BaseDocument {

	@Id
	@Field(type = FieldType.Keyword)
	private String storeId;

	// --- Store 기본 정보 ---
	// 1. 이름 (검색용 Text + 정렬용 Keyword)
	// text: "맛있는 삼겹살" -> "맛있는", "삼겹살" (형태소 분석, 검색용)
	// keyword: "맛있는 삼겹살" -> "맛있는 삼겹살" (원문 그대로, 정렬용)
	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword)
		}
	)
	private String name;

	@Field(type = FieldType.Keyword)
	private StoreStatus status; // "OPERATING", "CLOSED" 등 문자열로 저장

	@Field(type = FieldType.Text, analyzer = "nori")
	private String address;

	@Field(type = FieldType.Keyword)
	private String phoneNumber;

	@GeoPointField
	private GeoPoint location;

	@Field(type = FieldType.Integer)
	private Integer totalCapacity;

	@Field(type = FieldType.Boolean)
	private Boolean isHot;

	@Field(type = FieldType.Keyword)
	private String imageUrl;

	@Field(type = FieldType.Text)
	private String description;

	@Field(type = FieldType.Integer)
	private Integer turnoverRateMinutes;

	// @Field(type = FieldType.Date, format = DateFormat.hour_minute_second) // 시간 범위 탐색용
	@Field(type = FieldType.Keyword)
	private LocalTime waitingOpenTime;

	// @Field(type = FieldType.Date, format = DateFormat.hour_minute_second) // 시간 범위 탐색용
	@Field(type = FieldType.Keyword)
	private LocalTime reservationOpenTime;

	// --- Category (역정규화: 검색 성능을 위해 이름 리스트로 저장) ---
	@Field(type = FieldType.Text, analyzer = "nori")
	private List<String> categories; // 예: ["한식", "삼겹살"]

	@Field(type = FieldType.Keyword)
	private List<String> categoryIds;

	// 3. 정렬을 위한 통계 데이터 (MVP 요구사항)
	@Field(type = FieldType.Double)
	private Double averageRating;  // 평점

	@Field(type = FieldType.Integer)
	private Long reviewCount;   // 리뷰 수

	// // // --- OperatingHour (Nested Object) ---
	// // @Field(type = FieldType.Nested)
	// // private List<OperatingHourInfo> operatingHours;
	// //
	// // // --- StoreReservationPolicy (1:1 관계이므로 Flattening하여 필드로 포함) ---
	// // // 정책 정보는 필터링("예약금 없는 곳", "6명 가능")에 중요하므로 최상위 레벨 혹은 Object로 둡니다.
	// // @Field(type = FieldType.Object)
	// // private ReservationPolicyInfo reservationPolicy;
	//
	// // --- 내부 클래스 정의 ---
	// @Getter
	// @Builder
	// public static class OperatingHourInfo {
	// 	@Field(type = FieldType.Keyword)
	// 	private String dayOfWeek;
	// 	@Field(type = FieldType.Keyword)
	// 	private LocalTime openTime;
	// 	@Field(type = FieldType.Keyword)
	// 	private LocalTime closeTime;
	// 	@Field(type = FieldType.Boolean)
	// 	private boolean isClosed;
	// }
	//
	// @Getter
	// @Builder
	// public static class ReservationPolicyInfo {
	// 	@Field(type = FieldType.Integer)
	// 	private int monthlyOpenDay;
	//
	// 	@Field(type = FieldType.Integer)
	// 	private int reservationInterval;
	//
	// 	@Field(type = FieldType.Integer)
	// 	private int minHeadcount;
	//
	// 	@Field(type = FieldType.Integer)
	// 	private int maxHeadcount;
	//
	// 	@Field(type = FieldType.Boolean)
	// 	private boolean isDepositRequired;
	//
	// 	@Field(type = FieldType.Integer)
	// 	private int depositAmount;
	// }

	// 초기 생성을 위한 팩토리 메서드
	public static StoreDocument create(String storeId, StoreUpdateCommand command) {
		GeoPoint geoPoint = (command.latitude() != null && command.longitude() != null)
			? new GeoPoint(command.latitude().doubleValue(), command.longitude().doubleValue()) : null;

		return StoreDocument.builder()
			.storeId(storeId)
			.name(command.name())
			.status(StoreStatus.valueOf(command.storeStatus()))
			.address(command.address())
			.location(geoPoint)
			.totalCapacity(command.totalCapacity())
			.isHot(command.isHot())
			.imageUrl(command.imageUrl())
			.description(command.description())
			.turnoverRateMinutes(command.turnoverRateMinutes())
			.waitingOpenTime(command.waitingOpenTime())
			.reservationOpenTime(command.reservationOpenTime())
			.categories(command.categories())
			.categoryIds(command.categoryIds())
			// 초기 생성 시 통계는 0
			.averageRating(0.0)
			.reviewCount(0L)
			.createdAt(command.createdAt())
			.createdBy(command.createdBy())
			.updatedAt(command.updatedAt())
			.updatedBy(command.updatedBy())
			.deletedAt(command.deletedAt())
			.deletedBy(command.deletedBy())
			.build();
	}

	public void updateBasicInfo(StoreUpdateCommand command) {
		this.name = command.name()
			!= null ? command.name() : this.name;
		this.status = command.storeStatus()
			!= null ? StoreStatus.valueOf(command.storeStatus()) : this.status; // 상태 변경도 여기서 반영
		this.phoneNumber = command.phoneNumber()
			!= null ? command.phoneNumber() : this.phoneNumber;
		this.address = command.address()
			!= null ? command.address() : this.address;
		this.totalCapacity = command.totalCapacity()
			!= null ? command.totalCapacity() : this.totalCapacity;
		this.isHot = command.isHot()
			!= null ? command.isHot() : this.isHot;
		this.imageUrl = command.imageUrl()
			!= null ? command.imageUrl() : this.imageUrl;
		this.description = command.description()
			!= null ? command.description() : this.description;
		this.turnoverRateMinutes = command.turnoverRateMinutes()
			!= null ? command.turnoverRateMinutes() : this.turnoverRateMinutes;
		this.waitingOpenTime = command.waitingOpenTime()
			!= null ? command.waitingOpenTime() : this.waitingOpenTime;
		this.reservationOpenTime = command.reservationOpenTime()
			!= null ? command.reservationOpenTime() : this.reservationOpenTime;
		this.categories = command.categories()
			!= null ? command.categories() : this.categories;
		this.categoryIds = command.categoryIds()
			!= null ? command.categoryIds() : this.categoryIds;
		super.update(command.updatedAt(), command.updatedBy());

		if (command.latitude() != null && command.longitude() != null) {
			this.location = new GeoPoint(command.latitude().doubleValue(), command.longitude().doubleValue());
		}
		// 필요 시 categories, imageUrl 등 업데이트
	}

	// 통계 정보만 업데이트하는 메서드
	public void updateReviewStats(Double averageRating, Long reviewCount) {
		this.averageRating = averageRating;
		this.reviewCount = reviewCount;
	}

	public void softDelete(LocalDateTime deletedAt, UUID deleterId) {
		super.delete(deletedAt, deleterId);
	}
}
