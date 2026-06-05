package com.tablekok.hotreservationservice.application.dto;

import java.util.List;

/**
 * 스케줄러/예약 종료 시 Redis pub/sub 으로 주고받는 메시지.
 *
 * <ul>
 *   <li>{@code TICK}  : 한 틱의 입장+대기 갱신을 한 번에 전달. 입장 유저는 {@code entryUserIds} 로 명시,
 *       대기 유저는 리스트 없이 {@code updateCount} 만 보내 구독자가 자기 emitter 전체에 브로드캐스트한다.</li>
 *   <li>{@code DONE}  : 특정 유저의 예약 종료 알림.</li>
 * </ul>
 */
public record QueueMessage(
	Type type,
	List<String> entryUserIds,
	String entryTtl,
	String updateCount,
	String doneUserId
) {

	public enum Type {
		TICK, DONE
	}

	public static QueueMessage tick(List<String> entryUserIds, String entryTtl, String updateCount) {
		return new QueueMessage(Type.TICK, entryUserIds, entryTtl, updateCount, null);
	}

	public static QueueMessage done(String userId) {
		return new QueueMessage(Type.DONE, null, null, null, userId);
	}
}
