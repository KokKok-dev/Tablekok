package com.tablekok.gateway_service.filter;

import com.tablekok.gateway_service.util.JwtValidator;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 🛡️ JWT 인증 필터 (Gateway Service용)
 *
 * 📋 역할: 모든 요청의 JWT 토큰 검증 및 인가
 * 1. Authorization 헤더에서 JWT 토큰 추출
 * 2. 토큰 유효성 검증
 * 3. 토큰에서 사용자 정보 추출
 * 4. 경로별 권한 확인
 * 5. 인가 성공 시 요청을 해당 마이크로서비스로 라우팅
 *
 * 🚫 인증(로그인)은 User Service에서 담당
 * Gateway는 인가(권한 확인)만 담당
 *
 * ⚠️ Spring Cloud Gateway 특성상 @Autowired 필드 주입 사용
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	/**
	 * JwtValidator 의존성 주입
	 * Spring Cloud Gateway 필터는 생성자 주입보다 필드 주입이 안전함
	 */
	@Autowired
	private JwtValidator jwtValidator;

	/**
	 * 기본 생성자 (AbstractGatewayFilterFactory 요구사항)
	 */
	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	/**
	 * Gateway 필터 로직 구현
	 *
	 * @param config 필터 설정
	 * @return Gateway 필터
	 */
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			String path = request.getURI().getPath();
			String method = request.getMethod().toString();
			log.info("🌐 Processing request: {} {}", method, path);

			// 📂 인증이 필요없는 경로는 바로 통과
			if (isPublicPath(path)) {
				log.info("✅ Public path accessed: {}", path);
				return chain.filter(exchange);
			}

			// 🔑 Authorization 헤더에서 토큰 추출
			String authHeader = request.getHeaders().getFirst("Authorization");
			String token = jwtValidator.extractTokenFromHeader(authHeader);

			if (token == null) {
				log.warn("❌ Missing JWT token for protected path: {}", path);
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}

			try {
				// 🔍 JWT 토큰 유효성 검증
				if (!jwtValidator.validateToken(token)) {
					log.warn("❌ Invalid JWT token for path: {}", path);
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					return response.setComplete();
				}

				// 👤 토큰에서 사용자 정보 추출
				UUID userId = jwtValidator.getUserIdFromToken(token);
				String email = jwtValidator.getEmailFromToken(token);
				String role = jwtValidator.getRoleFromToken(token);

				log.debug("🔍 Extracted user info - ID: {}, Email: {}, Role: {}",
					userId, email, role);

				// 🛡️ 경로별 권한 확인
				if (!jwtValidator.hasPermissionForPath(path, role)) {
					log.warn("🚫 Access denied for user {} with role {} to path {}",
						userId, role, path);
					response.setStatusCode(HttpStatus.FORBIDDEN);
					return response.setComplete();
				}

				// ✅ 인가 성공: 사용자 정보를 헤더에 추가하여 다음 서비스로 전달
				ServerHttpRequest modifiedRequest = request.mutate()
					.header("X-User-Id", userId.toString())      // 사용자 ID 전달
					.header("X-User-Email", email)               // 사용자 이메일 전달
					.header("X-User-Role", role)                 // 사용자 역할 전달
					.header("X-Gateway-Verified", "true")        // Gateway 검증 완료 표시
					.build();

				log.info("✅ Authorization successful - User: {} ({}), Role: {}, Path: {}",
					userId, email, role, path);

				// 🚀 검증된 요청을 다음 마이크로서비스로 전달
				return chain.filter(exchange.mutate().request(modifiedRequest).build());

			} catch (JwtException e) {
				log.error("🔥 JWT processing error for path {}: {}", path, e.getMessage());
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			} catch (IllegalArgumentException e) {
				log.error("🔥 Invalid UUID format in token for path {}: {}", path, e.getMessage());
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			} catch (Exception e) {
				log.error("🔥 Unexpected error during authentication for path {}: {}",
					path, e.getMessage(), e);
				response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
				return response.setComplete();
			}
		};
	}

	/**
	 * 📂 인증이 필요없는 공개 경로 확인
	 *
	 * @param path 요청 경로
	 * @return 공개 경로 여부
	 */
	private boolean isPublicPath(String path) {
		return path.startsWith("/v1/auth/login") ||           // 🔐 로그인
			path.startsWith("/v1/auth/signup") ||          // 📝 회원가입
			path.startsWith("/v1/users/findid") ||         // 🔍 ID 찾기
			path.startsWith("/v1/users/findpassword") ||   // 🔍 비밀번호 찾기
			path.startsWith("/actuator/health") ||         // 💚 헬스체크
			path.startsWith("/actuator/info");             // ℹ️ 서비스 정보
	}

	/**
	 * 필터 설정 클래스
	 * 향후 필터별 개별 설정이 필요한 경우 여기에 추가
	 */
	public static class Config {
		// 필터 설정이 필요한 경우 여기에 추가
		// 예: 특정 경로별 다른 토큰 검증 로직 등
	}
}