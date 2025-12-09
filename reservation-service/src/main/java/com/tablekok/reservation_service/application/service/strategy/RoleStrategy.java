package com.tablekok.reservation_service.application.service.strategy;

import java.util.UUID;

import com.tablekok.entity.UserRole;

public interface RoleStrategy {
	Boolean supports(UserRole role);

	void cancelReservation(UUID userId, UUID reservationId);
}
