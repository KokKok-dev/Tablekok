package com.tablekok.search_service.infrastructure.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		Map<String, Object> config = new HashMap<>();

		// 1. 기본 서버 설정
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		// 2. 역직렬화(Deserialization) 설정
		// Key는 String, Value는 JSON으로 처리
		// ErrorHandlingDeserializer: 역직렬화 실패 시 무한루프 방지 및 에러 로깅용 래퍼
		return new DefaultKafkaConsumerFactory<>(
			config,
			new StringDeserializer(),
			new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Object.class)
				.trustedPackages("*") // ★ 중요: 모든 패키지의 DTO를 신뢰함
				.forKeys() // Key가 아닌 Value에 대한 설정임을 명시하지 않으면 기본값
			)
		);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		// 필요하다면 에러 핸들러 설정 가능 (예: 재시도 로직 등)
		// factory.setCommonErrorHandler(...);

		return factory;
	}
}
