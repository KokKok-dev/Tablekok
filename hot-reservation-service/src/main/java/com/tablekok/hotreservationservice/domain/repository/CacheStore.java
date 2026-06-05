package com.tablekok.hotreservationservice.domain.repository;

import java.util.Collection;
import java.util.Set;

public interface CacheStore {

	// 대기열에 추가 (이미 있으면 순번 유지 — 재연결 대비)
	void addUserToQueueIfAbsent(String userId, long sseTtl);

	// 대기 순번 반환
	Long getRank(String userId);

	// 대기열 상위 count 명 조회 (입장 처리 대상)
	Set<String> getTopUsers(long count);

	// 유저들을 예약 가능 공간에 일괄 추가
	void addAvailableUsers(Collection<String> userIds, long entryTtl);

	// 대기열에서 사용자 삭제
	void removeUserFromQueue(String userId);

	// 대기열에서 사용자 일괄 삭제
	void removeUsersFromQueue(Collection<String> userIds);

	// 입장 유저 조회
	Double findAvailableUser(String userId);

	// 유저를 예약 가능 공간에서 삭제
	void removeAvailableUser(String userId);

	// 만료된 유저 삭제
	void removeExpiredAvailableUsers(long now);

	// 현재 예약중인 유저 숫자
	int getAvailableUserCount();

	// pub/sub 이벤트 발행
	void convertAndSend(String message);
}
