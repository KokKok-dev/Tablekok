package com.tablekok.review_service.infrastructure.kafka;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.tablekok.review_service.application.client.ReviewMessagePort;
import com.tablekok.review_service.application.dto.ReviewStatsEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventProducer implements ReviewMessagePort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	private static final String TOPIC = "store-review-stats";

	@Override
	public void sendReviewStats(UUID storeId, Double averageRating, Long reviewCount) {
		ReviewStatsEvent event = ReviewStatsEvent.of(storeId, averageRating, reviewCount);

		kafkaTemplate.send(TOPIC, storeId.toString(), event)
			.whenComplete((result, ex) -> {
				if (ex == null) {
					log.info("Kafka Event Sent: topic={}, storeId={}, rating={}",
						TOPIC, storeId, averageRating);
				} else {
					log.error("Failed to send Kafka event: storeId={}", storeId, ex);
				}
			});
	}
}
