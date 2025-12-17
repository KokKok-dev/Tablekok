package com.tablekok.gateway_service.filter;

import com.tablekok.gateway_service.util.JwtValidator;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Autowired
	private JwtValidator jwtValidator;

	private static final List<String> PUBLIC_PATHS = Arrays.asList(
		// User Service 인증 경로
		"/v1/auth/login",
		"/v1/auth/signup/customer",
		"/v1/auth/signup/owner",
		"/v1/users/findid",
		"/v1/users/findpassword",

		// 운영 관련 경로 (MSA 필수)
		"/actuator/health",
		"/actuator/info",
		"/actuator/metrics",

		// API 문서 경로 (개발 편의성)
		"/swagger-ui",
		"/v3/api-docs",
		"/user-service/swagger-ui",
		"/user-service/v3/api-docs",
		"/user-service/actuator"
	);

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			String path = request.getURI().getPath();

			log.debug("JWT 인가 필터 - 경로: {}", path);

			// 공개 경로 체크
			if (isPublicPath(path)) {
				log.debug("공개 경로 - 인가 스킵: {}", path);
				return chain.filter(exchange);
			}

			// JWT 토큰 추출
			String token = jwtValidator.extractTokenFromHeader(
				request.getHeaders().getFirst("Authorization")
			);

			if (token == null) {
				log.warn("JWT 토큰이 없습니다 - 경로: {}", path);
				return handleUnauthorized(exchange, "토큰이 없습니다");
			}

			// JWT 토큰 검증
			if (!jwtValidator.validateToken(token)) {
				log.warn("JWT 토큰이 유효하지 않습니다 - 경로: {}", path);
				return handleUnauthorized(exchange, "유효하지 않은 토큰입니다");
			}

			try {
				// 토큰에서 사용자 정보 추출
				Claims claims = jwtValidator.getClaimsFromToken(token);
				String userId = claims.getSubject();
				String email = claims.get("email", String.class);
				String role = claims.get("role", String.class);

				log.debug("JWT 검증 성공 - 사용자: {}, 역할: {}, 경로: {}", userId, role, path);

				// 인증된 사용자 정보를 헤더에 추가하여 후속 서비스로 전달
				ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
					.header("X-User-Id", userId)
					.header("X-User-Email", email)
					.header("X-User-Role", role)
					.header("X-Gateway-Verified", "true")
					.build();

				log.debug("사용자 정보 헤더 추가 완료 - 경로: {}", path);

				return chain.filter(exchange.mutate().request(modifiedRequest).build());

			} catch (Exception e) {
				log.error("JWT 토큰 처리 중 오류 발생 - 경로: {}, 오류: {}", path, e.getMessage());
				return handleUnauthorized(exchange, "토큰 처리 중 오류 발생");
			}
		};
	}

	private boolean isPublicPath(String path) {
		return PUBLIC_PATHS.stream().anyMatch(publicPath ->
			path.startsWith(publicPath) || path.contains(publicPath)
		);
	}

	private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		String body = String.format(
			"{\"error\":\"Unauthorized\",\"message\":\"%s\",\"status\":401}",
			message
		);

		log.debug("401 응답 전송 - 메시지: {}", message);

		// 올바른 WebFlux 방식으로 응답 작성
		DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Mono.just(buffer));
	}

	private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.FORBIDDEN);
		response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		String body = String.format(
			"{\"error\":\"Forbidden\",\"message\":\"%s\",\"status\":403}",
			message
		);

		log.debug("403 응답 전송 - 메시지: {}", message);

		// 올바른 WebFlux 방식으로 응답 작성
		DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Mono.just(buffer));
	}

	public static class Config {
		// Configuration properties can be added here
	}
}
