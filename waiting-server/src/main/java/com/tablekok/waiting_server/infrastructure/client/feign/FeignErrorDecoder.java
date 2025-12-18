package com.tablekok.waiting_server.infrastructure.client.feign;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablekok.exception.AppException;
import com.tablekok.waiting_server.application.exception.WaitingErrorCode;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			// 상대 service 가 보낸 에러 응답 Body를 String으로 읽기
			String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
			log.error("Feign 호출 에러 발생 [{}]: {}", methodKey, body);

			// JSON 파싱해서 "message" 필드만 추출
			JsonNode jsonNode = objectMapper.readTree(body);
			String errorMessage = jsonNode.path("message").asText("외부 서비스 에러가 발생했습니다.");

			// EXTERNAL_SERVICE_ERROR 같은 공통 코드를 쓰되, 메시지만 치환
			return new AppException(WaitingErrorCode.INVALID_REQUEST, errorMessage);

		} catch (IOException e) {
			log.error("에러 메시지 파싱 중 오류 발생", e);
			return new AppException(WaitingErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
