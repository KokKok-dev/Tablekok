package com.tablekok.hotreservationservice.infrastructure.Cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedissonLockMapper {
	private final RedissonClient redissonClient;

	public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> task) {
		RLock lock = redissonClient.getLock("lock:" + key);
		try {
			// 락 획득 시도
			boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
			if (!available) {
				throw new RuntimeException("현재 예약 요청이 많아 처리가 지연되고 있습니다. 다시 시도해주세요.");
			}

			// 실제 비즈니스 로직 실행
			return task.get();

		} catch (InterruptedException e) {
			throw new RuntimeException("락 획득 중 인터럽트 발생", e);
		} finally {
			// 내가 잡은 락이면 해제
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
