package com.tablekok.store_service.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tablekok.entity.UserRole;
import com.tablekok.exception.AppException;
import com.tablekok.store_service.application.dto.command.UpdateStoreStatusCommand;
import com.tablekok.store_service.application.exception.StoreErrorCode;
import com.tablekok.store_service.application.service.strategy.StoreStatusTransitionStrategy;
import com.tablekok.store_service.application.service.strategy.StrategyFactory;
import com.tablekok.store_service.domain.entity.Store;
import com.tablekok.store_service.domain.entity.StoreStatus;
import com.tablekok.store_service.domain.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
public class StoreStatusServiceTest {

	// 1. 서비스에 의존성 주입
	@InjectMocks
	private StoreService storeService;

	// 2. 의존 객체 Mocking
	@Mock
	private StoreRepository storeRepository;

	@Mock
	private StrategyFactory strategyFactory;

	// 3. 전략 객체 Mocking (팩토리에서 반환될 객체)
	@Mock
	private StoreStatusTransitionStrategy ownerStrategy;

	private UUID storeId;
	private Store store;

	private final UserRole ownerRole = UserRole.OWNER;

	@BeforeEach
	void setUp() {
		storeId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		this.store = Store.of(
			ownerId, "Test Store", "010-1234-5678", "Address",
			new BigDecimal("0"), new BigDecimal("0"), "Desc", 100, 30, "img.jpg"
		);
	}

	@Test
	@DisplayName("[Fail] OWNER 는 PENDING_APPROVAL 일때 상태 변경 불가 ")
	void updateStatus_OwnerForbiddenTransition_ThrowsAppException() {
		// given : PENDING_APPROVAL 상태일 때 Owner가 OPERATING으로 상태 변경하려고 하는 상황
		StoreStatus targetNewStatus = StoreStatus.OPERATING;
		UpdateStoreStatusCommand forbiddenCommand = new UpdateStoreStatusCommand(targetNewStatus.name());

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(strategyFactory.getStrategy(ownerRole)).thenReturn(ownerStrategy);

		doThrow(new AppException(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION))
			.when(ownerStrategy)
			.changeStatus(any(Store.class), any(StoreStatus.class));

		// when
		AppException exception = assertThrows(AppException.class, () -> {
			storeService.updateStatus(ownerRole, storeId, forbiddenCommand);
		});

		// then
		assertEquals(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION, exception.getErrorCode(),
			"예상된 예외 코드가 발생해야 합니다.");

	}
}
