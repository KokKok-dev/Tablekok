package com.tablekok.user_service.auth.domain.repository;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User 도메인 Repository 인터페이스
 * 도메인 계층에서 정의하는 순수한 인터페이스
 * 구체적인 구현은 Infrastructure 계층에서 담당
 */
public interface UserRepository {

	// ========== 기본 CRUD 메서드 ==========

	/**
	 * 사용자 저장
	 */
	User save(User user);

	/**
	 * 사용자 ID로 조회
	 */
	Optional<User> findById(UUID userId);

	/**
	 * 모든 사용자 조회 (페이징)
	 */
	Page<User> findAll(Pageable pageable);

	/**
	 * 사용자 삭제
	 */
	void delete(User user);

	/**
	 * 사용자 ID로 삭제
	 */
	void deleteById(UUID userId);

	// ========== 비즈니스 조회 메서드 ==========

	/**
	 * 이메일로 사용자 조회
	 */
	Optional<User> findByEmail(String email);

	/**
	 * 휴대폰번호로 사용자 조회
	 */
	Optional<User> findByPhoneNumber(String phoneNumber);

	/**
	 * 이메일과 이름으로 사용자 조회 (ID 찾기용)
	 */
	Optional<User> findByEmailAndName(String email, String name);

	/**
	 * 이름과 휴대폰번호로 사용자 조회
	 */
	Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

	// ========== 존재 여부 확인 메서드 ==========

	/**
	 * 이메일 존재 여부 확인
	 */
	boolean existsByEmail(String email);

	/**
	 * 휴대폰번호 존재 여부 확인
	 */
	boolean existsByPhoneNumber(String phoneNumber);

	/**
	 * 사용자 ID 존재 여부 확인
	 */
	boolean existsById(UUID userId);

	// ========== 역할 기반 조회 메서드 ==========

	/**
	 * 역할별 사용자 조회 (페이징)
	 */
	Page<User> findByRole(UserRole role, Pageable pageable);

	/**
	 * 활성 상태별 사용자 조회 (페이징)
	 */
	Page<User> findByIsActive(Boolean isActive, Pageable pageable);

	/**
	 * 역할과 활성 상태로 사용자 조회 (페이징)
	 */
	Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable);

	// ========== 검색 메서드 ==========

	/**
	 * 이름으로 사용자 검색 (부분 일치, 페이징)
	 */
	Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

	/**
	 * 이메일으로 사용자 검색 (부분 일치, 페이징)
	 */
	Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	/**
	 * 이름 또는 이메일로 사용자 검색 (통합 검색, 페이징)
	 */
	Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String name, String email, Pageable pageable);

	// ========== 통계 및 집계 메서드 ==========

	/**
	 * 전체 사용자 수
	 */
	long count();

	/**
	 * 역할별 사용자 수
	 */
	long countByRole(UserRole role);

	/**
	 * 활성 사용자 수
	 */
	long countByIsActive(Boolean isActive);

	/**
	 * 특정 기간 내 가입한 사용자 조회
	 */
	List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	/**
	 * 특정 기간 내 로그인한 사용자 조회
	 */
	List<User> findByLastLoginAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	// ========== 정렬 및 필터링 메서드 ==========

	/**
	 * 가입일 기준 최신순 사용자 조회 (페이징)
	 */
	Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

	/**
	 * 최근 로그인 기준 사용자 조회 (페이징)
	 */
	Page<User> findAllByOrderByLastLoginAtDesc(Pageable pageable);

	/**
	 * 로그인 횟수 기준 사용자 조회 (페이징)
	 */
	Page<User> findAllByOrderByLoginCountDesc(Pageable pageable);

	// ========== 관리자용 고급 조회 메서드 ==========

	/**
	 * 비활성 사용자 조회 (일정 기간 로그인하지 않은 사용자)
	 */
	List<User> findInactiveUsers(LocalDateTime cutoffDate);

	/**
	 * 최근 가입 사용자 조회 (관리자 모니터링용)
	 */
	List<User> findRecentSignups(int days);

	/**
	 * 역할별 최근 가입자 조회
	 */
	List<User> findRecentSignupsByRole(UserRole role, int days);
}
