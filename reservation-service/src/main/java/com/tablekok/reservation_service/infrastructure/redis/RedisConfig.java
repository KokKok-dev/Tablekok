package com.tablekok.reservation_service.infrastructure.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password:pass}")
	private String password;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();

		String schema = (host.contains("amazonaws.com")) ? "rediss://" : "redis://";
		String address = schema + host + ":" + port;

		config.useSingleServer()
			.setAddress(address);

		if (!"pass".equals(password)) {
			config.useSingleServer().setPassword(password);
		}

		return Redisson.create(config);
	}
}
