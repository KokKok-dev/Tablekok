package com.tablekok.search_service.domain.document;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseDocument {

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Keyword) // UUID는 형태소 분석 불필요 -> Keyword
	private UUID createdBy;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime updatedAt;

	@Field(type = FieldType.Keyword)
	private UUID updatedBy;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime deletedAt;

	@Field(type = FieldType.Keyword)
	private UUID deletedBy;

	// 검색 필터링을 위한 Soft Delete 플래그 (BaseEntity의 isDeleted() 메서드 대응)
	@Field(type = FieldType.Boolean)
	private boolean isDeleted;

	public void update(LocalDateTime updatedAt, UUID updatedBy) {
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
	}

	/**
	 * 삭제 처리 편의 메서드
	 */
	public void delete(LocalDateTime deletedAt, UUID deleterId) {
		this.isDeleted = true;
		this.deletedAt = deletedAt;
		this.deletedBy = deleterId;
	}
}
