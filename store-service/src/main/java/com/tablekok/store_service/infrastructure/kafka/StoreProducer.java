package com.tablekok.store_service.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tablekok.store_service.application.dto.event.StoreEvent;
import com.tablekok.store_service.application.port.StoreEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreProducer implements StoreEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private static final String TOPIC = "store-events";

	@Override
	public void publish(StoreEvent event) {
		log.info("Sending store event to Kafka: storeId={}, type={}", event.storeId(), event.operationType());

		kafkaTemplate.send(TOPIC, event.storeId().toString(), event);
	}
}
