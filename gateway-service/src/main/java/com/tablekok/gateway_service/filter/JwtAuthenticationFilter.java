package com.tablekok.gateway_service.filter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.tablekok.gateway_service.util.JwtValidator;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Autowired
	private JwtValidator jwtValidator;

	@Autowired
	private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	@Value("${redis.auth.invalid-before-prefix}")
	private String invalidBeforePrefix;

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

	@Data
	public static class Config {
		private boolean required = true;
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
				if (config.isRequired()) {
					log.warn("JWT 토큰이 없습니다 (인증 필수) - 경로: {}", path);
					return handleUnauthorized(exchange, "토큰이 없습니다");
				}
				log.debug("JWT 토큰이 없습니다 (선택적 인증) - 경로: {}", path);
				return chain.filter(exchange);
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
				Date issuedAt = claims.getIssuedAt();

				log.debug("JWT 검증 성공 - 사용자: {}, 역할: {}, 경로: {}", userId, role, path);

				// Redis 블랙리스트 체크 (비밀번호 변경 여부)
				return checkTokenInvalidation(userId, issuedAt)
					.flatMap(isInvalid -> {
						if (isInvalid) {
							log.warn("토큰이 무효화되었습니다 (비밀번호 변경) - 사용자: {}", userId);
							return handleUnauthorized(exchange, "비밀번호가 변경되어 재로그인이 필요합니다");
						}

						// 인증된 사용자 정보를 헤더에 추가
						ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
							.header("X-User-Id", userId)
							.header("X-User-Email", email)
							.header("X-User-Role", role)
							.header("X-Gateway-Verified", "true")
							.build();

						log.debug("사용자 정보 헤더 추가 완료 - 경로: {}", path);

						return chain.filter(exchange.mutate().request(modifiedRequest).build());
					});

			} catch (Exception e) {
				log.error("JWT 토큰 처리 중 오류 발생 - 경로: {}, 오류: {}", path, e.getMessage());
				return handleUnauthorized(exchange, "토큰 처리 중 오류 발생");
			}
		};
	}

	/**
	 * Redis에서 토큰 무효화 여부 체크
	 * - invalid-before 시점 이전에 발급된 토큰이면 무효
	 */
	private Mono<Boolean> checkTokenInvalidation(String userId, Date issuedAt) {
		String key = invalidBeforePrefix + userId;

		return reactiveRedisTemplate.opsForValue().get(key)
			.map(invalidBeforeStr -> {
				Instant invalidBefore = Instant.parse(invalidBeforeStr);
				Instant tokenIssuedAt = issuedAt.toInstant();
				// 토큰 발급시간이 무효화 시점 이전이면 true (무효)
				return tokenIssuedAt.isBefore(invalidBefore);
			})
			.defaultIfEmpty(false);  // Redis에 값이 없으면 유효한 토큰
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

		DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Mono.just(buffer));
	}
}
