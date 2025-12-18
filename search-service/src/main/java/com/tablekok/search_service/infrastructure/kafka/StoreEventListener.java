package com.tablekok.search_service.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tablekok.search_service.application.dto.event.ReviewStatsEvent;
import com.tablekok.search_service.application.dto.event.StoreEvent;
import com.tablekok.search_service.application.service.StoreCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreEventListener {

	private final StoreCommandService storeCommandService;

	/**
	 * Store-Service의 변경 사항 구독 (Topic: store-events)
	 */
	@KafkaListener(
		topics = "store-events",
		groupId = "search-service-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void handleStoreEvent(StoreEvent event) {
		try {
			storeCommandService.syncStoreData(event);
		} catch (Exception e) {
			log.error("Failed to process store event: {}", event, e);
			// 필요 시 Dead Letter Queue(DLQ)로 보내거나 재시도 로직 추가
		}
	}

	/**
	 * Review-Service의 통계 변경 사항 구독 (Topic: store-review-stats)
	 */
	@KafkaListener(
		topics = "store-review-stats",
		groupId = "search-service-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void handleReviewStatsEvent(ReviewStatsEvent event) {
		try {
			storeCommandService.syncReviewStats(event);
		} catch (Exception e) {
			log.error("Failed to process review stats event: {}", event, e);
			// 가게 정보가 아직 없을 때 발생할 수 있음 -> 재시도 필요
			throw e; // Kafka Consumer가 예외를 감지하고 Retry 하도록 던짐
		}
	}
}
