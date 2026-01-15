package com.tablekok.user_service.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(
			secret.getBytes(
				StandardCharsets.UTF_8));
	}

	public String generateAccessToken(UUID userId, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

		return Jwts.builder()
			.setSubject(userId.toString())
			.claim("role", role)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(UUID userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public UUID getUserIdFromToken(String token) {
		String subject = Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
		return UUID.fromString(subject);
	}
}
