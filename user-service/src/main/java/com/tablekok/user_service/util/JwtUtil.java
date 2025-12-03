package com.tablekok.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tablekok.user_service.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 유틸리티 클래스 (User Service용)
 *
 * 📋 역할: 인증(Authentication) 담당
 * - 로그인 시 JWT 토큰 발급
 * - 회원가입 시 즉시 JWT 토큰 발급
 *
 * JWT 검증/파싱은 Gateway Service에서 담당
 * 인증과 인가의 명확한 책임 분리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	/**
	 * JWT 서명에 사용할 비밀키 생성
	 * ⚠Gateway와 동일한 비밀키 사용 필요 (Config Server에서 중앙 관리)
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 액세스 토큰 생성
	 *
	 * 포함 정보:
	 * - subject: 사용자 UUID
	 * - email: 사용자 이메일
	 * - role: 사용자 역할 (Gateway 인가 시 사용)
	 * - type: ACCESS (토큰 타입 구분)
	 *
	 * @param userId 사용자 UUID
	 * @param email 사용자 이메일
	 * @param role 사용자 역할 (CUSTOMER, OWNER)
	 * @return 생성된 JWT 액세스 토큰
	 */
	public String generateAccessToken(UUID userId, String email, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

		String token = Jwts.builder()
			.setSubject(userId.toString())              // 토큰 주체 (사용자 ID)
			.claim("email", email)                      // 사용자 이메일
			.claim("role", role)                        // 사용자 역할 (Gateway 인가용)
			.claim("type", "ACCESS")                    // 토큰 타입 구분
			.setIssuedAt(now)                          // 토큰 발급 시간
			.setExpiration(expiryDate)                 // 토큰 만료 시간
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 서명 알고리즘
			.compact();

		log.info("Access token generated for user: {}, role: {}", userId, role);
		return token;
	}

	/**
	 * 리프레시 토큰 생성
	 *
	 * 액세스 토큰 갱신용으로 더 긴 만료 시간 설정
	 * 최소한의 정보만 포함 (보안 강화)
	 *
	 * @param userId 사용자 UUID
	 * @return 생성된 JWT 리프레시 토큰
	 */
	public String generateRefreshToken(UUID userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

		String token = Jwts.builder()
			.setSubject(userId.toString())              // 토큰 주체 (사용자 ID)
			.claim("type", "REFRESH")                   // 토큰 타입 구분
			.setIssuedAt(now)                          // 토큰 발급 시간
			.setExpiration(expiryDate)                 // 토큰 만료 시간
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 서명 알고리즘
			.compact();

		log.info("Refresh token generated for user: {}", userId);
		return token;
	}

	/**
	 * 토큰 생성 로그용 정보 생성
	 *
	 * @param userId 사용자 ID
	 * @param email 사용자 이메일
	 * @param role 사용자 역할
	 * @return 로그용 토큰 정보
	 */
	public String getTokenInfo(UUID userId, String email, String role) {
		return String.format("[TokenGenerated] UserId: %s, Email: %s, Role: %s",
			userId, email, role);
	}
}
