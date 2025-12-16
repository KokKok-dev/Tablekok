package com.tablekok.user_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.tablekok.user_service.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(UUID userId, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

		return Jwts.builder()
			.setSubject(userId.toString())
			.claim("role", role)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

}
