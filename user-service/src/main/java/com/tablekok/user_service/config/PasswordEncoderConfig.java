package com.tablekok.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 설정 클래스
 *
 * 역할: 사용자 비밀번호 암호화/검증
 * - 회원가입 시 비밀번호 BCrypt 암호화
 * - 로그인 시 비밀번호 검증
 * - 비밀번호 변경 시 암호화
 *
 * BCrypt 알고리즘 특징:
 * - Salt 자동 생성 (동일 비밀번호라도 다른 해시값)
 * - 계산 비용 조절 가능 (브루트 포스 공격 방어)
 * - Spring Security 표준 권장 방식
 */
@Configuration
public class PasswordEncoderConfig {

	/**
	 * BCrypt 비밀번호 암호화 빈 등록
	 *
	 * BCrypt 설정:
	 * - 기본 strength: 10 (2^10 = 1024 rounds)
	 * - 더 높은 보안이 필요하면 12 이상 설정 가능
	 * - 너무 높으면 성능 저하 발생
	 *
	 * 사용 예시:
	 * String rawPassword = "user123!";
	 * String encodedPassword = passwordEncoder.encode(rawPassword);
	 * boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
	 *
	 * @return PasswordEncoder 구현체
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// BCrypt 해시 함수 사용 (strength: 10)
		return new BCryptPasswordEncoder();
	}
}
