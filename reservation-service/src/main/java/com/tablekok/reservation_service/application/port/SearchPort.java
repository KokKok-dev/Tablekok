package com.tablekok.reservation_service.application.port;

import java.util.UUID;

import com.tablekok.reservation_service.application.port.dto.response.GetReservationPolicyResponse;

public interface SearchPort {
	GetReservationPolicyResponse getReservationPolicy(UUID storeId);

	boolean checkStoreOwner(UUID userId, UUID storeId);
}
