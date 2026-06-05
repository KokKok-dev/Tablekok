package com.tablekok.hotreservationservice.application.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.hotreservationservice.domain.repository.CacheStore;
import com.tablekok.hotreservationservice.infrastructure.Cache.CacheStoreImpl;

/**
 * 스케줄러 입장 처리(processAllUsers)의 PUBLISH 발행 비용을 전/후로 측정하는 수동 성능 테스트.
 *
 * <p>측정 지표
 * <ul>
 *   <li>A. 틱 소요시간: processAllUsers(count) wall-clock (ms)</li>
 *   <li>B. PUBLISH 호출 수: CacheStore.convertAndSend 호출 횟수 (Mockito spy 로 카운트)</li>
 * </ul>
 *
 * <p>전제: 로컬 Redis 가 떠 있어야 한다(기본 localhost:6379 / pw systempass — dev 설정 기준).
 * 환경변수 REDIS_HOST / REDIS_PORT / REDIS_PASSWORD 로 덮어쓸 수 있다.
 *
 * <p>CI 에서 자동 실행되지 않도록 환경변수 RUN_REDIS_PERF=true 일 때만 활성화된다.
 * 실행 예 (PowerShell):
 * <pre>
 *   $env:RUN_REDIS_PERF="true"; ./gradlew :hot-reservation-service:test --tests "*QueueServicePublishPerfTest" --info
 * </pre>
 * 결과는 콘솔과 build/queue-perf-result.txt 양쪽에 남는다.
 */
@EnabledIfEnvironmentVariable(named = "RUN_REDIS_PERF", matches = "true")
class QueueServicePublishPerfTest {

	private static final String QUEUE_KEY = "reservation:queue";
	private static final String AVAILABLE_USERS_KEY = "reservation:available_users";
	private static final String PUB_SUB_CHANNEL = "waiting-queue";

	private static final long ENTRY_TTL = 30_000L;
	private static final int ENTRY_COUNT = 50; // 한 틱에 입장시키는 인원 (availableUserLimit 기준)

	private LettuceConnectionFactory connectionFactory;
	private RedisTemplate<String, String> redisTemplate;

	@BeforeEach
	void setUp() {
		String host = envOrDefault("REDIS_HOST", "localhost");
		int port = Integer.parseInt(envOrDefault("REDIS_PORT", "6379"));
		String password = envOrDefault("REDIS_PASSWORD", "systempass");

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		if (password != null && !password.isBlank()) {
			config.setPassword(password);
		}
		connectionFactory = new LettuceConnectionFactory(config);
		connectionFactory.afterPropertiesSet();

		redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
	}

	@AfterEach
	void tearDown() {
		if (redisTemplate != null) {
			redisTemplate.delete(List.of(QUEUE_KEY, AVAILABLE_USERS_KEY));
		}
		if (connectionFactory != null) {
			connectionFactory.destroy();
		}
	}

	@ParameterizedTest
	@ValueSource(ints = {1_000, 5_000, 10_000})
	void measurePublishCost(int queueSize) {
		// 1) 큐 초기화 후 N명 시드
		redisTemplate.delete(List.of(QUEUE_KEY, AVAILABLE_USERS_KEY));
		for (int i = 0; i < queueSize; i++) {
			redisTemplate.opsForZSet().add(QUEUE_KEY, "user-" + i, i);
		}

		// 2) 실제 Redis 를 쓰는 CacheStore + PUBLISH 카운트용 spy
		CacheStoreImpl realStore = new CacheStoreImpl(redisTemplate);
		ReflectionTestUtils.setField(realStore, "QUEUE_KEY", QUEUE_KEY);
		ReflectionTestUtils.setField(realStore, "AVAILABLE_USERS_KEY", AVAILABLE_USERS_KEY);
		ReflectionTestUtils.setField(realStore, "PUB_SUB_CHANNEL", PUB_SUB_CHANNEL);
		CacheStore store = Mockito.spy(realStore);

		QueueService service = new QueueService(store, new ObjectMapper());
		ReflectionTestUtils.setField(service, "ENTRY_TTL", ENTRY_TTL);

		// 3) 측정: 틱 소요시간(A)
		long start = System.nanoTime();
		service.processAllUsers(ENTRY_COUNT);
		long elapsedMs = (System.nanoTime() - start) / 1_000_000;

		// 4) 측정: PUBLISH 호출 수(B)
		long publishCalls = Mockito.mockingDetails(store).getInvocations().stream()
			.filter(inv -> "convertAndSend".equals(inv.getMethod().getName()))
			.count();

		record(queueSize, elapsedMs, publishCalls);
	}

	private void record(int queueSize, long elapsedMs, long publishCalls) {
		String line = String.format(
			"[QUEUE-PERF] queueSize=%d  entryCount=%d  elapsedMs=%d  publishCalls=%d",
			queueSize, ENTRY_COUNT, elapsedMs, publishCalls);
		System.out.println(line);
		try {
			Path out = Path.of("build", "queue-perf-result.txt");
			Files.createDirectories(out.getParent());
			Files.writeString(out, line + System.lineSeparator(),
				StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("[QUEUE-PERF] 결과 파일 기록 실패: " + e.getMessage());
		}
	}

	private static String envOrDefault(String key, String def) {
		String v = System.getenv(key);
		return (v == null || v.isBlank()) ? def : v;
	}
}
