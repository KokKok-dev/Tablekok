package com.tablekok.hotreservationservice.application.service;

import java.util.Map;
import java.util.Set;

public interface CacheStore {

	// 대기열에 추가
	void addUserToQueue(String userId, long score);

	// 대기 순번 반환
	Long getRank(String userId);

	// 현재 대기 중인 모든 사용자 조회
	Set<String> getAllUsers();

	// 토큰을 저장
	void saveToken(String userId, String token);

	// 유저를 예약 가능 공간에 추가
	void addAvailableUser(String userId, long expirationTime);

	// 대기열에서 사용자 삭제
	void removeUserFromQueue(String userId);

	// 유저의 토큰 조회
	String getToken(String userId);

	// 유저의 토큰 삭제
	void removeToken(String userId);

	// 유저를 예약 가능 공간에서 삭제
	void removeAvailableUser(String userId);

	// 예약 가능한 사용자 조회
	Map<Object, Object> getAvailableUsers();

}
