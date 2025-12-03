package com.tablekok.store_service.presentation.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.tablekok.store_service.application.dto.param.CreateOperatingHourParam;
import com.tablekok.store_service.application.dto.param.CreateStoreParam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(
	@NotBlank(message = "음식점 이름은 필수입니다.")
	String name,

	@NotBlank(message = "전화번호는 필수입니다.")
	String phoneNumber,

	@NotBlank(message = "주소는 필수입니다.")
	String address,

	@NotNull(message = "위도는 필수입니다.")
	BigDecimal latitude,

	@NotNull(message = "경도는 필수입니다.")
	BigDecimal longitude,

	@Size(max = 500, message = "소개글은 500자 이내여야 합니다.")
	String description,

	@NotNull(message = "총 수용 인원수는 필수입니다.")
	@Min(value = 1, message = "최소 1명 이상입니다.")
	Integer totalCapacity,

	@NotNull(message = "식사 회전율은 필수입니다.")
	@Min(value = 1, message = "최소 1분 이상입니다.")
	Integer turnoverRateMinutes,

	String imageUrl,

	@NotNull(message = "카테고리 정보는 필수입니다.")
	@Size(min = 1, max = 3, message = "카테고리는 최소 1개, 최대 3개까지 설정 가능합니다.")
	List<UUID> categoryIds,

	@NotNull(message = "운영 시간 정보는 필수입니다.")
	@Size(min = 1, message = "최소 하나의 운영 시간 정보가 필요합니다.")
	@Valid
	List<CreateOperatingHourRequest> operatingHours
) {

	public CreateStoreParam toParam(UUID ownerId) {

		List<CreateOperatingHourParam> operatingHourParams = this.operatingHours.stream()
			.map(CreateOperatingHourRequest::toParam)
			.toList();

		return CreateStoreParam.builder()
			.ownerId(ownerId)
			.name(name)
			.phoneNumber(phoneNumber)
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.description(description)
			.totalCapacity(totalCapacity)
			.turnoverRateMinutes(turnoverRateMinutes)
			.imageUrl(imageUrl)
			.categoryIds(categoryIds)
			.operatingHours(operatingHourParams)
			.build();
	}
}
