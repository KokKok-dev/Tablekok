package com.tablekok.hotreservationservice.domain.repository;

import java.util.Set;

public interface CacheStore {

	// 대기열에 추가
	void addUserToQueue(String userId, long entryTtl);

	// 대기 순번 반환
	Long getRank(String userId);

	// 현재 대기 중인 모든 사용자 조회
	Set<String> getAllUsers();

	// 유저를 예약 가능 공간에 추가
	void addAvailableUser(String userId, long entryTtl);

	// 대기열에서 사용자 삭제
	void removeUserFromQueue(String userId);

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
