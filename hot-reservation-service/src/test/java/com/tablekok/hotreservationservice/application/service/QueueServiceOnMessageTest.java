package com.tablekok.hotreservationservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.hotreservationservice.application.dto.QueueMessage;
import com.tablekok.hotreservationservice.domain.repository.CacheStore;

/**
 * 구독 측(onMessage → handleTick/handleDone) 라우팅 로직 단위 테스트.
 * Redis 불필요 — emitters 맵에 가짜 SseEmitter 를 심고 메시지를 흘려보내 검증한다.
 */
class QueueServiceOnMessageTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private QueueService service;
	private Map<String, SseEmitter> emitters;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		service = new QueueService(mock(CacheStore.class), objectMapper);
		// private final emitters 맵 참조를 꺼내 테스트가 직접 심는다
		emitters = (Map<String, SseEmitter>) ReflectionTestUtils.getField(service, "emitters");
	}

	@Test
	void tick_sends_entry_to_listed_and_broadcasts_update_to_all() throws Exception {
		SseEmitter e1 = mock(SseEmitter.class); // 입장 대상
		SseEmitter e2 = mock(SseEmitter.class); // 대기
		emitters.put("u1", e1);
		emitters.put("u2", e2);

		service.onMessage(objectMapper.writeValueAsString(
			QueueMessage.tick(List.of("u1"), "30000", "2")));

		// u1: entry + update = 2번
		ArgumentCaptor<SseEmitter.SseEventBuilder> c1 = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
		verify(e1, times(2)).send(c1.capture());
		List<String> p1 = c1.getAllValues().stream()
			.map(QueueServiceOnMessageTest::payload)
			.collect(Collectors.toList());
		assertThat(p1).anyMatch(s -> s.contains("event:entry") && s.contains("data:30000"));
		assertThat(p1).anyMatch(s -> s.contains("event:update") && s.contains("data:2"));

		// u2: update 1번만
		ArgumentCaptor<SseEmitter.SseEventBuilder> c2 = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
		verify(e2, times(1)).send(c2.capture());
		assertThat(payload(c2.getValue())).contains("event:update").contains("data:2");
	}

	@Test
	void tick_skips_entry_user_not_connected_here() throws Exception {
		SseEmitter e2 = mock(SseEmitter.class);
		emitters.put("u2", e2); // u1 은 다른 서버에 연결됨(여기 없음)

		service.onMessage(objectMapper.writeValueAsString(
			QueueMessage.tick(List.of("u1"), "30000", "2")));

		// NPE 없이, u2 는 update 만 받는다
		verify(e2, times(1)).send(any(SseEmitter.SseEventBuilder.class));
	}

	@Test
	void done_completes_and_removes_emitter() throws Exception {
		SseEmitter e1 = mock(SseEmitter.class);
		emitters.put("u1", e1);

		service.onMessage(objectMapper.writeValueAsString(QueueMessage.done("u1")));

		ArgumentCaptor<SseEmitter.SseEventBuilder> c = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
		verify(e1).send(c.capture());
		assertThat(payload(c.getValue())).contains("event:done");
		verify(e1).complete();
		assertThat(emitters).doesNotContainKey("u1");
	}

	// SseEventBuilder 는 내용을 바로 노출하지 않으므로 build() 로 펼쳐 문자열로 검증한다.
	// 결과 예: "id:u1\nevent:entry\ndata:30000\n\n"
	private static String payload(SseEmitter.SseEventBuilder builder) {
		return builder.build().stream()
			.map(data -> String.valueOf(data.getData()))
			.collect(Collectors.joining());
	}
}
