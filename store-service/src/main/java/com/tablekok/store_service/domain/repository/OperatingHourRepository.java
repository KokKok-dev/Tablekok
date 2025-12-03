package com.tablekok.store_service.domain.repository;

import java.util.List;

import com.tablekok.store_service.domain.entity.OperatingHour;

public interface OperatingHourRepository {

	void saveAll(List<OperatingHour> operatingHours);
}
