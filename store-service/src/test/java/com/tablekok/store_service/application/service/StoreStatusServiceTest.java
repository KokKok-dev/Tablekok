package com.tablekok.store_service.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

	@InjectMocks
	private StoreService storeService;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private StrategyFactory strategyFactory;

	@Mock
	private StoreStatusTransitionStrategy mockStrategy;

	private final UUID storeId = UUID.randomUUID();
	private Store mockStore;
	private final String testRole = "OWNER";
	private final StoreStatus targetStatus = StoreStatus.OPERATING;
	private UpdateStoreStatusCommand command;

	@BeforeEach
	void setUp() {
		// 테스트용 Mock Store 엔티티 (실제 데이터 필드는 중요하지 않음)
		mockStore = mock(Store.class);
		when(mockStore.getId()).thenReturn(storeId);

		// Command 객체 생성
		command = new UpdateStoreStatusCommand(targetStatus.name());
	}

	@Test
	@DisplayName("[Fail] OWNER 는 PENDING_APPROVAL 일때 상태 변경 불가 ")
	void updateStatus_StrategyThrowsError_PropagatesException() {
		// given
		StoreStatus forbiddenStatus = StoreStatus.PENDING_APPROVAL;
		UpdateStoreStatusCommand forbiddenCommand = new UpdateStoreStatusCommand(forbiddenStatus.name());

		AppException strategyError = new AppException(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION);

		// when
		doThrow(strategyError).when(mockStrategy).changeStatus(eq(mockStore), eq(forbiddenStatus));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(strategyFactory.getStrategy(testRole)).thenReturn(mockStrategy);

		AppException exception = assertThrows(AppException.class, () ->
			storeService.updateStatus(testRole, storeId, forbiddenCommand));

		// then
		assertEquals(StoreErrorCode.OWNER_FORBIDDEN_STATUS_TRANSITION, exception.getErrorCode());
		verify(storeRepository, never()).save(any());

	}
}
