package com.tablekok.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Getter
@Component
public class JwtConfig {

	@Value("${jwt.secret:tablekok-dev-secret-key-for-local-development-only}")
	private String secret;

	@Value("${jwt.access-token-expiration:3600000}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration:604800000}")
	private long refreshTokenExpiration;
}
