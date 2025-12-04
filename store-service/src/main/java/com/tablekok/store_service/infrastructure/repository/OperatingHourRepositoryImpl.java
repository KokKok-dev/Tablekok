package com.tablekok.store_service.infrastructure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.tablekok.store_service.domain.entity.OperatingHour;
import com.tablekok.store_service.domain.repository.OperatingHourRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OperatingHourRepositoryImpl implements OperatingHourRepository {
	private final OperatingHourJpaRepository operatingHourJpaRepository;

	@Override
	public List<OperatingHour> saveAll(List<OperatingHour> operatingHours) {
		return operatingHourJpaRepository.saveAll(operatingHours);
	}
}
