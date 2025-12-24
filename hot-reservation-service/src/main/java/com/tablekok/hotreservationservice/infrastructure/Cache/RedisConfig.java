package com.tablekok.hotreservationservice.infrastructure.Cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import com.tablekok.hotreservationservice.application.service.QueueService;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "60s") // 기본 락 유지 시간
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password:pass}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

		LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder = LettuceClientConfiguration.builder();
		if ("localhost".equals(host)) {
			config.setPassword(password);
		}
		if (!"localhost".equals(host)) {
			clientConfigBuilder.useSsl();
		}
		LettuceClientConfiguration clientConfig = clientConfigBuilder.build();

		return new LettuceConnectionFactory(config, clientConfig);
	}

	@Bean
	public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
		return new RedisLockProvider(connectionFactory);
	}

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

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		// "waiting-queue"라는 이름의 채널을 구독합니다.
		container.addMessageListener(listenerAdapter, new ChannelTopic("waiting-queue"));

		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(QueueService queueService) {
		return new MessageListenerAdapter(queueService, "onMessage");
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		// 가독성을 위해 Key/Value를 문자열로 직렬화
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		// Hash 사용 시 설정
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}
}
