package com.tablekok.gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway Service 메인 애플리케이션 클래스
 *
 * 주요 기능:
 * - API 라우팅 및 로드 밸런싱
 * - JWT 토큰 검증 및 인가 (Authorization)
 * - 마이크로서비스 간 통신 관리
 * - 공통 필터링 (CORS, 로깅 등)
 *
 * 인가 담당: "누구에게 무엇을 허용할지" 결정
 *
 * Spring Cloud Gateway는 WebFlux 기반
 * - @EnableFeignClients 불필요 (WebClient 사용)
 * - 리액티브 프로그래밍 모델 적용
 */
@SpringBootApplication(
	scanBasePackages = {
		"com.tablekok.gateway_service",      // 현재 서비스 패키지 스캔
		"com.tablekok"                       // Common 모듈 컴포넌트 스캔
	}
)
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

}