package com.tablekok.gateway_service.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtValidator {

	@Value("${jwt.secret}")
	private String secretKey;

	public static final String BEARER_PREFIX = "Bearer ";

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(
			secretKey.getBytes(
				StandardCharsets.UTF_8));
	}

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

	public boolean validateToken(String token) {
		try {
			getClaimsFromToken(token);
			return true;
		} catch (JwtException e) {
			log.debug("JWT 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public String extractTokenFromHeader(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
			return authorizationHeader.substring(BEARER_PREFIX.length());
		}
		return null;
	}
}
