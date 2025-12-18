package com.tablekok.search_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.search_service.application.dto.event.ReviewStatsEvent;
import com.tablekok.search_service.application.dto.event.StoreEvent;
import com.tablekok.search_service.domain.document.StoreDocument;
import com.tablekok.search_service.domain.repository.StoreSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StoreCommandService {

	private final StoreSearchRepository storeSearchRepository;

	/**
	 * [Store-Service Event] 가게 정보 동기화
	 * CREATE(생성), UPDATE(수정), STATUS_CHANGE(상태변경), DELETE(삭제) 처리
	 */
	public void syncStoreData(StoreEvent event) {
		String storeId = event.storeId().toString();
		String opType = event.operationType();

		log.info("Syncing store data. ID: {}, Type: {}", storeId, opType);

		// 1. 삭제(DELETE) 처리
		if ("DELETE".equals(opType)) {
			softDeleteStore(storeId, event);
			return;
		}

		// 2. 생성 및 수정 (CREATE, UPDATE, STATUS_CHANGE)
		// 기존 문서가 있는지 확인 (Upsert 전략)
		storeSearchRepository.findById(storeId).ifPresentOrElse(
			// (A) 기존 문서 존재 -> 정보만 업데이트 (통계 유지)
			existingStore -> {
				existingStore.updateBasicInfo(event.toCommand());
				storeSearchRepository.save(existingStore);
				log.info("Updated existing store: {}", storeId);
			},
			// (B) 기존 문서 없음 -> 새로 생성
			() -> {
				StoreDocument newStore = StoreDocument.create(storeId, event.toCommand());
				storeSearchRepository.save(newStore);
				log.info("Created new store: {}", storeId);
			}
		);
	}

	/**
	 * [Review-Service Event] 리뷰 통계 동기화
	 */
	public void syncReviewStats(ReviewStatsEvent event) {
		String storeId = event.storeId().toString();

		storeSearchRepository.findById(storeId).ifPresentOrElse(
			store -> {
				// 통계 정보만 쏙 업데이트
				store.updateReviewStats(event.averageRating(), event.reviewCount());
				storeSearchRepository.save(store);
				log.info("Updated review stats for store: {}", storeId);
			},
			() -> {
				// 가게가 아직 Search 서버에 없는데 리뷰 통계가 먼저 온 경우
				// 1. 무시하거나
				// 2. Retryable Exception을 던져서 Kafka가 재시도하게 함 (추천)
				log.warn("Store not found for stats update. ID: {}", storeId);
				throw new RuntimeException("Store not found, retrying...");
			}
		);
	}

	// --- Private Helpers ---
	private void softDeleteStore(String storeId, StoreEvent event) {
		storeSearchRepository.findById(storeId).ifPresent(store -> {
			store.softDelete(event.deletedBy());
			storeSearchRepository.save(store);
			log.info("Soft deleted store: {}", storeId);
		});
	}
}
