package com.tablekok.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정 클래스 (User Service용)
 *
 * 역할: 인증(Authentication) 서비스를 위한 보안 설정
 * - JWT 기반 Stateless 인증
 * - 회원가입/로그인 엔드포인트 공개 설정
 * - CORS 설정
 * - CSRF 비활성화 (API 서버용)
 *
 * 인가(Authorization)는 Gateway에서 담당
 * User Service는 인증 로직에만 집중
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * 🛡Spring Security 필터 체인 설정
	 *
	 * JWT 기반 API 서버를 위한 최적화된 설정:
	 * - Stateless 세션 정책
	 * - 공개/보호 경로 구분
	 * - CORS 활성화
	 * - CSRF 비활성화
	 *
	 * @param http HttpSecurity 객체
	 * @return SecurityFilterChain
	 * @throws Exception 설정 에러
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CSRF 비활성화 (API 서버는 Stateless이므로 불필요)
			.csrf(csrf -> csrf.disable())

			// CORS 설정 활성화
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			// 세션 정책: Stateless (JWT 사용으로 세션 불필요)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// URL별 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				// 공개 경로 (인증 불필요)
				.requestMatchers(
					"/v1/auth/login",                    // 로그인
					"/v1/auth/signup/customer",          // 고객 회원가입
					"/v1/auth/signup/owner",             // 사장님 회원가입
					"/v1/users/findid",                  // ID 찾기
					"/v1/users/findpassword",            // 비밀번호 찾기
					"/actuator/health",                  // 헬스체크
					"/actuator/info",                    // 서비스 정보
					"/swagger-ui/**",                    // Swagger UI
					"/v3/api-docs/**"                    // API 문서
				).permitAll()

				// 나머지 모든 요청은 Gateway에서 검증된 요청만 허용
				.anyRequest().authenticated()
			)

			// 기본 HTTP Basic 인증 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())

			// 폼 로그인 비활성화 (API 서버이므로)
			.formLogin(formLogin -> formLogin.disable())

			// 로그아웃 설정 비활성화 (JWT는 클라이언트에서 토큰 삭제로 처리)
			.logout(logout -> logout.disable());

		return http.build();
	}

	/**
	 * CORS 설정
	 *
	 * 마이크로서비스 아키텍처에서 다양한 도메인 간 통신을 위한 CORS 설정
	 * 개발 환경에서는 모든 Origin 허용, 운영에서는 제한적 허용
	 *
	 * @return CORS 설정 소스
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용할 Origin 설정
		configuration.setAllowedOriginPatterns(Arrays.asList(
			"http://localhost:3000",        // React 개발 서버
			"http://localhost:8080",        // Gateway 서비스
			"https://*.tablekok.com",       // 운영 도메인
			"https://tablekok.vercel.app"   // 프론트엔드 배포 도메인
		));

		// 허용할 HTTP 메서드
		configuration.setAllowedMethods(Arrays.asList(
			"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
		));

		// 허용할 헤더
		configuration.setAllowedHeaders(Arrays.asList(
			"Authorization",        // JWT 토큰
			"Content-Type",         // JSON 요청
			"X-Requested-With",     // AJAX 요청 식별
			"X-User-Id",           // Gateway에서 전달하는 사용자 ID
			"X-User-Role"          // Gateway에서 전달하는 사용자 역할
		));

		// 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
		configuration.setAllowCredentials(true);

		// Preflight 요청 캐시 시간 (초)
		configuration.setMaxAge(3600L);

		// 응답에 노출할 헤더
		configuration.setExposedHeaders(Arrays.asList(
			"Authorization",        // 새로 발급된 토큰
			"X-Total-Count"         // 페이징용 총 개수
		));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
