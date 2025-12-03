package com.tablekok.reservation_service.application.strategy;

import java.util.UUID;

public interface RoleStrategy {
	Boolean supports(String role); //TODO Role 공통에 넣고 적용

	void cancelReservation(UUID userId, UUID reservationId);
}
