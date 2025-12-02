package com.tablekok.store_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tablekok.store_service.domain.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {
}
