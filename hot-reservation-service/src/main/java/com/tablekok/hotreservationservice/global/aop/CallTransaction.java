package com.tablekok.hotreservationservice.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CallTransaction {
	// 부모 트랜잭션 유무와 상관없이 별도의 트랜잭션으로 실행하여 락 점유 중 커밋 보장
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed();
	}
}
