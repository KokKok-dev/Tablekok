package com.tablekok.store_service.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.store_service.domain.entity.OperatingHour;

public interface OperatingHourJpaRepository extends JpaRepository<OperatingHour, Long> {
}
