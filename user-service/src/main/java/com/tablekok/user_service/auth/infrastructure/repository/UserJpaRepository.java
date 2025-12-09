package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User JPA Repository 인터페이스
 * Spring Data JPA 기술에 의존하는 Infrastructure 계층
 */
public interface UserJpaRepository extends JpaRepository<User, UUID> {

	// ========== 기본 조회 메서드 ==========

	Optional<User> findByEmail(String email);
	Optional<User> findByPhoneNumber(String phoneNumber);
	Optional<User> findByEmailAndName(String email, String name);
	Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

	// ========== 존재 여부 확인 메서드 ==========

	boolean existsByEmail(String email);
	boolean existsByPhoneNumber(String phoneNumber);

	// ========== 역할 기반 조회 메서드 ==========

	Page<User> findByRole(UserRole role, Pageable pageable);
	Page<User> findByIsActive(Boolean isActive, Pageable pageable);
	Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable);

	// ========== 검색 메서드 ==========

	Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
	Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
	Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String name, String email, Pageable pageable);

	// ========== 통계 메서드 ==========

	long countByRole(UserRole role);
	long countByIsActive(Boolean isActive);

	// ========== 기간별 조회 메서드 ==========

	List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
	List<User> findByLastLoginAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	// ========== 정렬 메서드 ==========

	Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
	Page<User> findAllByOrderByLastLoginAtDesc(Pageable pageable);
	Page<User> findAllByOrderByLoginCountDesc(Pageable pageable);

	// ========== 커스텀 쿼리 메서드 ==========

	/**
	 * 비활성 사용자 조회 (특정 날짜 이후 로그인하지 않은 사용자)
	 */
	@Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
	List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * 최근 가입 사용자 조회
	 */
	@Query("SELECT u FROM User u WHERE u.createdAt >= :cutoffDate ORDER BY u.createdAt DESC")
	List<User> findRecentSignups(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * 역할별 최근 가입자 조회
	 */
	@Query("SELECT u FROM User u WHERE u.role = :role AND u.createdAt >= :cutoffDate ORDER BY u.createdAt DESC")
	List<User> findRecentSignupsByRole(@Param("role") UserRole role, @Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * 활성 사용자만 조회 (삭제되지 않고 활성 상태인 사용자)
	 */
	@Query("SELECT u FROM User u WHERE u.isActive = true AND u.deletedAt IS NULL")
	Page<User> findActiveUsers(Pageable pageable);

	/**
	 * 로그인 횟수가 0인 미로그인 사용자 조회
	 */
	@Query("SELECT u FROM User u WHERE u.loginCount = 0")
	List<User> findNeverLoggedInUsers();

	/**
	 * 특정 기간 동안 활동한 사용자 수 (월별 통계용)
	 */
	@Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt BETWEEN :startDate AND :endDate")
	long countActiveUsersByPeriod(@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);

	/**
	 * 이메일 도메인별 사용자 수 조회 (통계용)
	 */
	@Query("SELECT SUBSTRING(u.email, LOCATE('@', u.email) + 1) as domain, COUNT(u) " +
		"FROM User u GROUP BY SUBSTRING(u.email, LOCATE('@', u.email) + 1) ORDER BY COUNT(u) DESC")
	List<Object[]> countByEmailDomain();
}
