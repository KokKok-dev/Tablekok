package com.tablekok.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Getter
@Component
public class JwtConfig {

	@Value("${jwt.secret:tablekok-dev-secret-key-for-local-development-only}")
	private String secret;

	@Value("${jwt.header:Authorization}")
	private String header;

	@Value("${jwt.prefix:Bearer }")
	private String prefix;
}
