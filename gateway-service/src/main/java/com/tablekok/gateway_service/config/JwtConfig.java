package com.tablekok.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

/**
 * JWT 설정 클래스 (Gateway Service용)
 *
 * User Service와 동일한 설정 사용 (Config Server에서 중앙 관리)
 * JWT 토큰 검증에 필요한 설정 포함
 */
@Getter
@Component
public class JwtConfig {

	/**
	 * JWT 토큰 서명 검증에 사용할 비밀키
	 * User Service와 동일한 키 사용 필수
	 */
	@Value("${jwt.secret:tablekok-dev-secret-key-for-local-development-only}")
	private String secret;

	/**
	 * HTTP 헤더에서 JWT 토큰을 찾을 헤더 이름
	 * 기본값: "Authorization"
	 */
	@Value("${jwt.header:Authorization}")
	private String header;

	/**
	 * JWT 토큰 앞에 붙는 접두사
	 * 기본값: "Bearer "
	 */
	@Value("${jwt.prefix:Bearer }")
	private String prefix;
}
