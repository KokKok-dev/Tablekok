package com.tablekok.store_service.application.port;

import com.tablekok.store_service.application.dto.event.StoreEvent;

public interface StoreEventPublisher {
	void publish(StoreEvent event);
}
