package com.tablekok.gateway_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tablekok.gateway_service.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT 토큰 검증 유틸리티 클래스 (Gateway Service용)
 *
 * 역할: 인가(Authorization) 담당
 * - JWT 토큰 유효성 검증
 * - 토큰에서 사용자 정보 추출
 * - 권한별 접근 제어
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final JwtConfig jwtConfig;

	/**
	 * JWT 서명 검증에 사용할 비밀키 생성
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * JWT 토큰에서 Claims 추출
	 */
	public Claims getClaimsFromToken(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			log.warn("JWT 토큰 만료: {}", e.getMessage());
			throw new JwtException("토큰이 만료되었습니다.");
		} catch (UnsupportedJwtException e) {
			log.warn("지원되지 않는 JWT 토큰: {}", e.getMessage());
			throw new JwtException("지원되지 않는 토큰입니다.");
		} catch (MalformedJwtException e) {
			log.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
			throw new JwtException("잘못된 형식의 토큰입니다.");
		} catch (SecurityException | IllegalArgumentException e) {
			log.warn("JWT 서명 검증 실패: {}", e.getMessage());
			throw new JwtException("토큰 서명 검증에 실패했습니다.");
		}
	}

	/**
	 * 토큰 유효성 검증
	 */
	public boolean validateToken(String token) {
		try {
			getClaimsFromToken(token);
			return true;
		} catch (JwtException e) {
			log.debug("JWT 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * HTTP 헤더에서 토큰 추출 (Bearer 접두사 제거)
	 */
	public String extractTokenFromHeader(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
			return authorizationHeader.substring(jwtConfig.getPrefix().length());
		}
		return null;
	}

	/**
	 * 경로별 권한 확인
	 */
	public boolean hasPermissionForPath(String path, String role) {
		log.debug("권한 확인 - 경로: {}, 역할: {}", path, role);

		// 공개 경로
		if (path.startsWith("/v1/auth/") ||
			path.startsWith("/actuator/")) {
			return true;
		}

		// 역할별 접근 제어
		switch (role) {
			case "CUSTOMER":
				return path.startsWith("/v1/users/profile") ||
					path.startsWith("/v1/users/findid") ||
					path.startsWith("/v1/users/findpassword");


			case "OWNER":
				return path.startsWith("/v1/users/profile") ||
					path.startsWith("/v1/users/findid") ||
					path.startsWith("/v1/users/findpassword") ||
					path.startsWith("/v1/stores/");

			case "MASTER":
				return true;  // 관리자는 모든 경로 접근 가능

			default:
				log.warn("알 수 없는 역할: {}", role);
				return false;
		}
	}
}
