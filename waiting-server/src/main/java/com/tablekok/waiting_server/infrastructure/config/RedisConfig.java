package com.tablekok.waiting_server.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

		// Redis 연결 팩토리를 설정합니다. (yml 설정이 여기서 사용됨)
		redisTemplate.setConnectionFactory(connectionFactory);

		// Key와 Value 직렬화 설정

		// 1. Key 직렬화: String 타입의 키를 사용하므로 StringRedisSerializer 사용
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer); // Hash Key도 String으로 설정

		// 2. Value 직렬화: ZSET의 멤버(UUID.toString())도 String이므로 StringRedisSerializer 사용
		redisTemplate.setValueSerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer); // Hash Value도 String으로 설정

		// 설정을 적용
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
