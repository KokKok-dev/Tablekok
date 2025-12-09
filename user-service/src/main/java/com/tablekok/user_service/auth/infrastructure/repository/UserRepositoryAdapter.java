package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository 구현체
 * Domain Repository 인터페이스를 Infrastructure에서 구현
 * gashine20 피드백: Repository 계층 분리 적용
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

	private final com.tablekok.user_service.auth.infrastructure.repository.UserJpaRepository userJpaRepository;

	// ========== 기본 CRUD 메서드 ==========

	@Override
	public User save(User user) {
		log.debug("Saving user with email: {}", user.getEmail());
		return userJpaRepository.save(user);
	}

	@Override
	public Optional<User> findById(UUID userId) {
		log.debug("Finding user by ID: {}", userId);
		return userJpaRepository.findById(userId);
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		log.debug("Finding all users with pageable: {}", pageable);
		return userJpaRepository.findAll(pageable);
	}

	@Override
	public void delete(User user) {
		log.debug("Deleting user with ID: {}", user.getUserId());
		userJpaRepository.delete(user);
	}

	@Override
	public void deleteById(UUID userId) {
		log.debug("Deleting user by ID: {}", userId);
		userJpaRepository.deleteById(userId);
	}

	// ========== 비즈니스 조회 메서드 ==========

	@Override
	public Optional<User> findByEmail(String email) {
		log.debug("Finding user by email: {}", email);
		return userJpaRepository.findByEmail(email);
	}

	@Override
	public Optional<User> findByPhoneNumber(String phoneNumber) {
		log.debug("Finding user by phone number: {}", phoneNumber);
		return userJpaRepository.findByPhoneNumber(phoneNumber);
	}

	@Override
	public Optional<User> findByEmailAndName(String email, String name) {
		log.debug("Finding user by email: {} and name: {}", email, name);
		return userJpaRepository.findByEmailAndName(email, name);
	}

	@Override
	public Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber) {
		log.debug("Finding user by name: {} and phone number: {}", name, phoneNumber);
		return userJpaRepository.findByNameAndPhoneNumber(name, phoneNumber);
	}

	// ========== 존재 여부 확인 메서드 ==========

	@Override
	public boolean existsByEmail(String email) {
		log.debug("Checking if email exists: {}", email);
		return userJpaRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByPhoneNumber(String phoneNumber) {
		log.debug("Checking if phone number exists: {}", phoneNumber);
		return userJpaRepository.existsByPhoneNumber(phoneNumber);
	}

	@Override
	public boolean existsById(UUID userId) {
		log.debug("Checking if user ID exists: {}", userId);
		return userJpaRepository.existsById(userId);
	}

	// ========== 역할 기반 조회 메서드 ==========

	@Override
	public Page<User> findByRole(UserRole role, Pageable pageable) {
		log.debug("Finding users by role: {} with pageable: {}", role, pageable);
		return userJpaRepository.findByRole(role, pageable);
	}

	@Override
	public Page<User> findByIsActive(Boolean isActive, Pageable pageable) {
		log.debug("Finding users by active status: {} with pageable: {}", isActive, pageable);
		return userJpaRepository.findByIsActive(isActive, pageable);
	}

	@Override
	public Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable) {
		log.debug("Finding users by role: {} and active status: {} with pageable: {}", role, isActive, pageable);
		return userJpaRepository.findByRoleAndIsActive(role, isActive, pageable);
	}

	// ========== 검색 메서드 ==========

	@Override
	public Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable) {
		log.debug("Searching users by name containing: {} with pageable: {}", name, pageable);
		return userJpaRepository.findByNameContainingIgnoreCase(name, pageable);
	}

	@Override
	public Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable) {
		log.debug("Searching users by email containing: {} with pageable: {}", email, pageable);
		return userJpaRepository.findByEmailContainingIgnoreCase(email, pageable);
	}

	@Override
	public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String name, String email, Pageable pageable) {
		log.debug("Searching users by name: {} or email: {} with pageable: {}", name, email, pageable);
		return userJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email, pageable);
	}

	// ========== 통계 및 집계 메서드 ==========

	@Override
	public long count() {
		log.debug("Counting all users");
		return userJpaRepository.count();
	}

	@Override
	public long countByRole(UserRole role) {
		log.debug("Counting users by role: {}", role);
		return userJpaRepository.countByRole(role);
	}

	@Override
	public long countByIsActive(Boolean isActive) {
		log.debug("Counting users by active status: {}", isActive);
		return userJpaRepository.countByIsActive(isActive);
	}

	@Override
	public List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
		log.debug("Finding users created between: {} and {}", startDate, endDate);
		return userJpaRepository.findByCreatedAtBetween(startDate, endDate);
	}

	@Override
	public List<User> findByLastLoginAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
		log.debug("Finding users with last login between: {} and {}", startDate, endDate);
		return userJpaRepository.findByLastLoginAtBetween(startDate, endDate);
	}

	// ========== 정렬 및 필터링 메서드 ==========

	@Override
	public Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable) {
		log.debug("Finding all users ordered by creation date desc with pageable: {}", pageable);
		return userJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
	}

	@Override
	public Page<User> findAllByOrderByLastLoginAtDesc(Pageable pageable) {
		log.debug("Finding all users ordered by last login desc with pageable: {}", pageable);
		return userJpaRepository.findAllByOrderByLastLoginAtDesc(pageable);
	}

	@Override
	public Page<User> findAllByOrderByLoginCountDesc(Pageable pageable) {
		log.debug("Finding all users ordered by login count desc with pageable: {}", pageable);
		return userJpaRepository.findAllByOrderByLoginCountDesc(pageable);
	}

	// ========== 관리자용 고급 조회 메서드 ==========

	@Override
	public List<User> findInactiveUsers(LocalDateTime cutoffDate) {
		log.debug("Finding inactive users with cutoff date: {}", cutoffDate);
		return userJpaRepository.findInactiveUsers(cutoffDate);
	}

	@Override
	public List<User> findRecentSignups(int days) {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
		log.debug("Finding recent signups within {} days, cutoff: {}", days, cutoffDate);
		return userJpaRepository.findRecentSignups(cutoffDate);
	}

	@Override
	public List<User> findRecentSignupsByRole(UserRole role, int days) {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
		log.debug("Finding recent signups by role: {} within {} days, cutoff: {}", role, days, cutoffDate);
		return userJpaRepository.findRecentSignupsByRole(role, cutoffDate);
	}
}
