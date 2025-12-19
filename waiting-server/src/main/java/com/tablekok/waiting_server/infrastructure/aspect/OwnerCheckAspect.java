package com.tablekok.waiting_server.infrastructure.aspect;

import java.util.UUID;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;
import com.tablekok.waiting_server.common.annotation.CheckOwner;
import com.tablekok.waiting_server.domain.entity.StoreWaitingStatus;
import com.tablekok.waiting_server.domain.repository.StoreWaitingStatusRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnerCheckAspect {
	private final StoreWaitingStatusRepository storeWaitingStatusRepository;

	// 메서드 인자 storeId로 시작 ~ onwerId로 끝나는 함수에 적용
	@Before("@annotation(checkOwner) && args(storeId, .., ownerId)")
	public void validateOwner(CheckOwner checkOwner, UUID storeId, UUID ownerId) {
		// 이제 여기서 전체 경로 없이 CheckOwner를 인식합니다.
		StoreWaitingStatus status = storeWaitingStatusRepository.findById(storeId)
			.orElseThrow(() -> new AppException(WaitingErrorCode.STORE_NOT_FOUND));

		status.validateOwner(ownerId);
	}

}
