package com.tablekok.gateway_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tablekok.gateway_service.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * 🛡️ JWT 토큰 검증 유틸리티 클래스 (Gateway Service용)
 *
 * 📋 역할: 인가(Authorization) 담당
 * - JWT 토큰 유효성 검증
 * - 토큰에서 사용자 정보 추출
 * - 권한별 접근 제어 지원
 *
 * 🚫 JWT 생성은 User Service에서 담당
 * 인증과 인가의 명확한 책임 분리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final JwtConfig jwtConfig;

	/**
	 * JWT 서명 검증에 사용할 비밀키 생성
	 * User Service와 동일한 비밀키 사용
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 🔍 JWT 토큰에서 Claims 추출
	 *
	 * @param token JWT 토큰
	 * @return Claims 객체 (토큰 내 데이터)
	 * @throws JwtException 토큰이 유효하지 않은 경우
	 */
	public Claims getClaimsFromToken(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			log.warn("🕐 JWT token is expired: {}", e.getMessage());
			throw new JwtException("토큰이 만료되었습니다.");
		} catch (UnsupportedJwtException e) {
			log.warn("❓ JWT token is unsupported: {}", e.getMessage());
			throw new JwtException("지원되지 않는 토큰입니다.");
		} catch (MalformedJwtException e) {
			log.warn("🔧 JWT token is malformed: {}", e.getMessage());
			throw new JwtException("잘못된 형식의 토큰입니다.");
		} catch (SecurityException | IllegalArgumentException e) {
			log.warn("🔒 JWT signature validation failed: {}", e.getMessage());
			throw new JwtException("토큰 서명 검증에 실패했습니다.");
		}
	}

	/**
	 * ✅ 토큰 유효성 검증
	 *
	 * @param token JWT 토큰
	 * @return 유효하면 true, 유효하지 않으면 false
	 */
	public boolean validateToken(String token) {
		try {
			getClaimsFromToken(token);
			return true;
		} catch (JwtException e) {
			log.debug("❌ JWT validation failed: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 👤 토큰에서 사용자 ID 추출
	 *
	 * @param token JWT 토큰
	 * @return 사용자 UUID
	 */
	public UUID getUserIdFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		String userId = claims.getSubject();
		return UUID.fromString(userId);
	}

	/**
	 * 📧 토큰에서 이메일 추출
	 *
	 * @param token JWT 토큰
	 * @return 사용자 이메일
	 */
	public String getEmailFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims.get("email", String.class);
	}

	/**
	 * 🎭 토큰에서 역할 추출 (권한 확인용)
	 *
	 * @param token JWT 토큰
	 * @return 사용자 역할 (CUSTOMER, OWNER)
	 */
	public String getRoleFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		return claims.get("role", String.class);
	}

	/**
	 * 🧹 HTTP 헤더에서 토큰 추출
	 * "Bearer " 접두사 제거
	 *
	 * @param authorizationHeader Authorization 헤더값
	 * @return 순수 JWT 토큰 (Bearer 제거된)
	 */
	public String extractTokenFromHeader(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
			return authorizationHeader.substring(jwtConfig.getPrefix().length());
		}
		return null;
	}

	/**
	 * 🔒 경로별 권한 확인
	 *
	 * @param path 요청 경로
	 * @param role 사용자 역할
	 * @return 접근 허용 여부
	 */
	public boolean hasPermissionForPath(String path, String role) {
		// 🌐 공통 접근 가능 경로
		if (path.startsWith("/v1/auth/")) {
			return true;  // 인증 관련 API는 모두 접근 가능
		}

		// 🎭 역할별 접근 제어
		switch (role) {
			case "CUSTOMER":
				return path.startsWith("/v1/users/profile/customer") ||
					path.startsWith("/v1/reservations/") ||
					path.startsWith("/v1/reviews/") ||
					path.startsWith("/v1/stores/search");  // 매장 검색은 고객도 가능

			case "OWNER":
				return path.startsWith("/v1/users/profile/owner") ||
					path.startsWith("/v1/stores/") ||
					path.startsWith("/v1/reservations/manage") ||
					path.startsWith("/v1/reviews/manage");

			default:
				log.warn("🚫 Unknown role: {}", role);
				return false;
		}
	}
}