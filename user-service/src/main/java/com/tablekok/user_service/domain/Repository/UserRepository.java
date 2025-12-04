package com.tablekok.user_service.domain.repository;

import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	// ========== 기본 조회 메서드 ==========

	/**
	 * 이메일로 사용자 조회 (로그인용)
	 */
	Optional<User> findByEmail(String email);

	/**
	 * 휴대폰번호로 사용자 조회 (중복 체크용)
	 */
	Optional<User> findByPhoneNumber(String phoneNumber);

	/**
	 * 이름과 휴대폰번호로 사용자 조회 (ID 찾기용)
	 */
	Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

	/**
	 * 이메일과 이름으로 사용자 조회 (비밀번호 찾기용)
	 */
	Optional<User> findByEmailAndName(String email, String name);

	// ========== 존재 여부 확인 메서드 ==========

	/**
	 * 이메일 중복 검증
	 */
	boolean existsByEmail(String email);

	/**
	 * 휴대폰번호 중복 검증
	 */
	boolean existsByPhoneNumber(String phoneNumber);

	/**
	 * 자신 제외 이메일 중복 검증 (정보 수정용)
	 */
	boolean existsByEmailAndUserIdNot(String email, UUID userId);

	/**
	 * 자신 제외 휴대폰번호 중복 검증 (정보 수정용)
	 */
	boolean existsByPhoneNumberAndUserIdNot(String phoneNumber, UUID userId);

	// ========== 활성 사용자 조회 메서드 ==========

	/**
	 * 활성화된 사용자만 이메일로 조회
	 */
	@Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true AND u.deletedAt IS NULL")
	Optional<User> findActiveUserByEmail(@Param("email") String email);

	/**
	 * 활성화된 사용자만 userId로 조회
	 */
	@Query("SELECT u FROM User u WHERE u.userId = :userId AND u.isActive = true AND u.deletedAt IS NULL")
	Optional<User> findActiveUserByUserId(@Param("userId") UUID userId);

	// ========== 역할별 조회 메서드 ==========

	/**
	 * 역할별 사용자 목록 조회 (페이징)
	 */
	Page<User> findByRoleAndDeletedAtIsNull(UserRole role, Pageable pageable);

	/**
	 * 특정 역할의 활성 사용자 수
	 */
	long countByRoleAndIsActiveTrueAndDeletedAtIsNull(UserRole role);

	/**
	 * OWNER 역할 사용자 조회 (사업자번호 포함)
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.owner WHERE u.role = :role AND u.deletedAt IS NULL")
	List<User> findOwnersWithBusinessInfo(@Param("role") UserRole role);

	// ========== 관리자용 조회 메서드 ==========

	/**
	 * 전체 회원 목록 조회 (삭제된 사용자 제외, 페이징)
	 */
	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ORDER BY u.createdAt DESC")
	Page<User> findAllActiveUsers(Pageable pageable);

	/**
	 * 검색 조건으로 사용자 조회
	 */
	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL " +
		"AND (:name IS NULL OR u.name LIKE %:name%) " +
		"AND (:email IS NULL OR u.email LIKE %:email%) " +
		"AND (:role IS NULL OR u.role = :role) " +
		"ORDER BY u.createdAt DESC")
	Page<User> findUsersWithSearchConditions(
		@Param("name") String name,
		@Param("email") String email,
		@Param("role") UserRole role,
		Pageable pageable
	);

	// ========== 로그인 관련 업데이트 메서드 ==========

	/**
	 * 로그인 정보 업데이트 (벌크 연산)
	 */
	@Modifying
	@Query("UPDATE User u SET u.lastLoginAt = :loginTime, u.loginCount = u.loginCount + 1 " +
		"WHERE u.userId = :userId")
	void updateLoginInfo(@Param("userId") UUID userId, @Param("loginTime") LocalDateTime loginTime);

	// ========== 통계 조회 메서드 ==========

	/**
	 * 특정 기간 내 가입자 수
	 */
	@Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.deletedAt IS NULL")
	long countNewUsersBetweenDates(@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);

	/**
	 * 역할별 총 사용자 수
	 */
	@Query("SELECT u.role, COUNT(u) FROM User u WHERE u.deletedAt IS NULL GROUP BY u.role")
	List<Object[]> countUsersByRole();

	/**
	 * 최근 로그인한 사용자들 조회
	 */
	@Query("SELECT u FROM User u WHERE u.lastLoginAt IS NOT NULL AND u.deletedAt IS NULL " +
		"ORDER BY u.lastLoginAt DESC")
	Page<User> findRecentlyActiveUsers(Pageable pageable);
}
