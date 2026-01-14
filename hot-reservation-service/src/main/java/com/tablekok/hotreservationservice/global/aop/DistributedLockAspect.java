package com.tablekok.hotreservationservice.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.tablekok.hotreservationservice.global.annotation.DistributedLock;
import com.tablekok.hotreservationservice.global.util.CustomSpringELParser;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@Order(1) // @Transactional보다 먼저 실행되도록 낮은 숫자로 우선순위 설정
@RequiredArgsConstructor
public class DistributedLockAspect {
	private final RedissonClient redissonClient;
	private final CallTransaction callTransaction;

	@Around("@annotation(distributedLock)")
	public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();

		// 1. SpEL 유틸을 사용하여 동적 키 생성
		String key = (String)CustomSpringELParser.getDynamicValue(
			signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

		RLock lock = redissonClient.getLock("lock:" + key);

		try {
			// 2. 락 획득 시도
			boolean available = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
				distributedLock.timeUnit());
			if (!available) {
				throw new RuntimeException("현재 요청이 많아 처리가 지연되고 있습니다.");
			}

			// 3. 트랜잭션이 보장된 메서드 대리 실행
			return callTransaction.proceed(joinPoint);
		} finally {
			// 4. 락 해제
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
