package com.tablekok.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

/**
 * JWT 설정 클래스 (User Service용)
 *
 * Config Server에서 JWT 설정을 중앙 관리
 * 토큰 생성에 필요한 설정만 포함
 */
@Getter
@Component
public class JwtConfig {

	/**
	 * JWT 토큰 서명에 사용할 비밀키
	 * Gateway와 동일한 키 사용 (Config Server에서 중앙 관리)
	 */
	@Value("${jwt.secret:tablekok-dev-secret-key-for-local-development-only}")
	private String secret;

	/**
	 * 액세스 토큰 만료 시간 (밀리초)
	 * 기본값: 1시간 (3600000ms)
	 */
	@Value("${jwt.access-token-expiration:3600000}")
	private long accessTokenExpiration;

	/**
	 * 리프레시 토큰 만료 시간 (밀리초)
	 * 기본값: 7일 (604800000ms)
	 */
	@Value("${jwt.refresh-token-expiration:604800000}")
	private long refreshTokenExpiration;
}
