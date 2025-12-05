package com.tablekok.user_service.auth.domain.service;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User 관련 Domain Service
 *
 * 주요 책임:
 * 1. User Entity 관련 복잡한 비즈니스 로직
 * 2. UserRepository 의존적인 검증
 * 3. User 도메인 규칙 검증
 * 4. Optional 처리 통합 관리
 *
 * 피드백: UserRepository 필요한 검증들 넣어서 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDomainService {

	private final UserRepository userRepository;

	// ========== User 존재 검증 (Optional 처리 통합) ==========

	/**
	 * 이메일로 사용자 조회 및 존재 검증
	 * 피드백: Optional 처리를 Domain Service에서 담당
	 *
	 * @param email 이메일
	 * @return 조회된 사용자
	 * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
	 */
	public User getUserByEmail(String email) {
		String normalizedEmail = User.normalizeEmail(email);
		User user = userRepository.findByEmail(normalizedEmail)
			.orElseThrow(() -> {
				log.warn("User not found for email: {}", normalizedEmail);
				return new IllegalArgumentException("가입되지 않은 이메일입니다.");
			});

		log.debug("User found for email: {}", normalizedEmail);
		return user;
	}

	/**
	 * 사용자 ID로 조회 및 존재 검증
	 * Optional 처리를 Domain Service에서 담당
	 *
	 * @param userId 사용자 ID
	 * @return 조회된 사용자
	 * @throws IllegalArgumentException 사용자가 존재하지 않는 경우
	 */
	public User getUserById(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				log.warn("User not found for ID: {}", userId);
				return new IllegalArgumentException("존재하지 않는 사용자입니다.");
			});

		log.debug("User found for ID: {}", userId);
		return user;
	}

	/**
	 * 사용자 ID 문자열로 조회 및 존재 검증
	 * UUID 형식 검증 + 존재 검증 통합
	 *
	 * @param userIdStr 사용자 ID 문자열
	 * @return 조회된 사용자
	 * @throws IllegalArgumentException UUID 형식 오류 또는 사용자 없음
	 */
	public User getUserByIdString(String userIdStr) {
		try {
			UUID userId = UUID.fromString(userIdStr);
			return getUserById(userId);
		} catch (IllegalArgumentException e) {
			log.warn("Invalid UUID format for user ID: {}", userIdStr);
			throw new IllegalArgumentException("올바르지 않은 사용자 ID 형식입니다.");
		}
	}

	// ========== User 중복 검증 ==========

	/**
	 * 이메일 중복 검증
	 *
	 * @param email 검증할 이메일
	 * @throws IllegalArgumentException 이미 가입된 이메일인 경우
	 */
	public void validateEmailNotDuplicated(String email) {
		String normalizedEmail = User.normalizeEmail(email);
		if (userRepository.existsByEmail(normalizedEmail)) {
			log.warn("Email duplication validation failed: {}", normalizedEmail);
			throw new IllegalArgumentException("이미 가입된 이메일입니다.");
		}
		log.debug("Email duplication check passed: {}", normalizedEmail);
	}

	/**
	 * 휴대폰번호 중복 검증
	 *
	 * @param phoneNumber 검증할 휴대폰번호
	 * @throws IllegalArgumentException 이미 가입된 휴대폰번호인 경우
	 */
	public void validatePhoneNumberNotDuplicated(String phoneNumber) {
		String normalizedPhone = User.normalizePhoneNumber(phoneNumber);
		if (userRepository.existsByPhoneNumber(normalizedPhone)) {
			log.warn("Phone number duplication validation failed: {}", normalizedPhone);
			throw new IllegalArgumentException("이미 가입된 휴대폰번호입니다.");
		}
		log.debug("Phone number duplication check passed: {}", normalizedPhone);
	}

	// ========== User 상태 검증 ==========

	/**
	 * 계정 활성 상태 검증
	 *
	 * @param user 검증할 사용자
	 * @throws IllegalStateException 계정이 비활성 상태인 경우
	 */
	public void validateAccountActive(User user) {
		if (!user.isAccountActive()) {
			log.warn("Account active validation failed for user: {}", user.getUserId());
			throw new IllegalStateException("비활성화된 계정입니다. 관리자에게 문의하세요.");
		}
		log.debug("Account active validation passed for user: {}", user.getUserId());
	}

	// ========== User 가용성 확인 ==========

	/**
	 * 이메일 사용 가능 여부 확인
	 *
	 * @param email 확인할 이메일
	 * @return 사용 가능하면 true
	 */
	public boolean isEmailAvailable(String email) {
		String normalizedEmail = User.normalizeEmail(email);
		boolean available = !userRepository.existsByEmail(normalizedEmail);
		log.debug("Email availability check for {}: {}", email, available);
		return available;
	}

	/**
	 * 휴대폰번호 사용 가능 여부 확인
	 *
	 * @param phoneNumber 확인할 휴대폰번호
	 * @return 사용 가능하면 true
	 */
	public boolean isPhoneNumberAvailable(String phoneNumber) {
		String normalizedPhone = User.normalizePhoneNumber(phoneNumber);
		boolean available = !userRepository.existsByPhoneNumber(normalizedPhone);
		log.debug("Phone number availability check: {}", available);
		return available;
	}

	// ========== User 통계 ==========

	/**
	 * 전체 사용자 수 조회
	 */
	public long getTotalUserCount() {
		long count = userRepository.count();
		log.debug("Total user count: {}", count);
		return count;
	}

	/**
	 * 역할별 사용자 수 조회
	 */
	public long getUserCountByRole(UserRole role) {
		long count = userRepository.countByRole(role);
		log.debug("User count for role {}: {}", role, count);
		return count;
	}

	/**
	 * 활성 사용자 수 조회
	 */
	public long getActiveUserCount() {
		long count = userRepository.countByIsActive(true);
		log.debug("Active user count: {}", count);
		return count;
	}

	/**
	 * 최근 가입자 수 조회 (일 단위)
	 */
	public long getRecentSignupCount(int days) {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
		List<User> users = userRepository.findByCreatedAtBetween(cutoffDate, LocalDateTime.now());
		long count = users.size();
		log.debug("Recent signup count for {} days: {}", days, count);
		return count;
	}
}
