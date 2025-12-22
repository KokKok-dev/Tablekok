package com.tablekok.waiting_server.application.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingBatchService {

	private final StoreWaitingStatusRepository storeWaitingStatusRepository;
	private final RedisTemplate<String, String> redisTemplate;

	private static final String WAITING_KEY_PREFIX = "waiting:queue:";

	@Transactional
	public void executeDailyReset() {
		storeWaitingStatusRepository.resetAllStoresDaily();
		log.info("DB: 모든 매장의 웨이팅 번호 및 상태 초기화 완료");

		String pattern = WAITING_KEY_PREFIX + "*";
		Set<String> keys = redisTemplate.keys(pattern);
		if (!keys.isEmpty()) {
			redisTemplate.delete(keys);
			log.info("Redis: {} 개의 웨이팅 키 삭제 완료", keys.size());
		}
	}
}
