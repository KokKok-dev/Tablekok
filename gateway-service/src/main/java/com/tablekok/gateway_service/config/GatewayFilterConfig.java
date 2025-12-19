package com.tablekok.gateway_service.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gateway 필터 설정 클래스 (백엔드 개발용)
 *
 * 역할: 개발 단계에서 필요한 최소한의 필터만 등록
 * - JWT 인증 필터 등록
 * - 개발용 로깅 필터 등록
 *
 * 운영용 복잡한 필터들은 제외 (추후 필요시 추가)
 *
 * 현재 등록된 필터들:
 * 1. developmentLoggingFilter - 요청/응답 로깅
 * 2. JwtAuthenticationFilter - JWT 검증 (application.yml에서 등록)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayFilterConfig {

	@Bean
	public GlobalFilter developmentLoggingFilter() {
		return (exchange, chain) -> {
			long startTime = System.currentTimeMillis();
			String requestPath = exchange.getRequest().getURI().getPath();
			String requestMethod = exchange.getRequest().getMethod().toString();
			String clientIp = getClientIP(exchange);

			log.info("[DEV] {} {} from {} - Start", requestMethod, requestPath, clientIp);

			return chain.filter(exchange)
				.doFinally(signalType -> {
					long duration = System.currentTimeMillis() - startTime;
					int statusCode = exchange.getResponse().getStatusCode() != null ?
						exchange.getResponse().getStatusCode().value() : 0;

					log.info("[DEV] {} {} - {} ({}ms)",
						requestMethod, requestPath, statusCode, duration);

					// 성능 경고 (2초 이상은 개발 단계에서도 주의)
					if (duration > 2000) {
						log.warn("[DEV] Slow request: {} {} took {}ms",
							requestMethod, requestPath, duration);
					}
				});
		};
	}

	@Bean
	public GlobalFilter developmentTraceFilter() {
		return (exchange, chain) -> {
			String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");

			// TraceId가 없으면 새로 생성 (개발용 간단한 형태)
			if (traceId == null || traceId.isEmpty()) {
				traceId = "dev-" + System.currentTimeMillis();
			}

			// 다음 서비스로 TraceId 전파
			exchange = exchange.mutate()
				.request(exchange.getRequest().mutate()
					.header("X-Trace-Id", traceId)
					.build())
				.build();

			// MDC에 TraceId 설정 (로깅용)
			org.slf4j.MDC.put("traceId", traceId);

			return chain.filter(exchange)
				.doFinally(signalType -> {
					// 🧹 MDC 정리
					org.slf4j.MDC.remove("traceId");
				});
		};
	}

	private String getClientIP(ServerWebExchange exchange) {
		// 개발환경에서는 단순한 IP 추출
		return exchange.getRequest().getRemoteAddress() != null ?
			exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "localhost";
	}
}
