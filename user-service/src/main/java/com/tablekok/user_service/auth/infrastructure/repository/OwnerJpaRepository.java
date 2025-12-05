package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerJpaRepository extends JpaRepository<Owner, UUID> {

	// ===============================
	// 기본 조회 메서드들
	// ===============================

	Optional<Owner> findByBusinessNumber(String businessNumber);

	/**
	 * ✅ User 객체를 받는 메서드 (OwnerRepositoryAdapter에서 실제 사용)
	 */
	@Query("SELECT o FROM Owner o WHERE o.user = :user")
	Optional<Owner> findByUser(@Param("user") User user);

	boolean existsByBusinessNumber(String businessNumber);

	/**
	 * ✅ 핵심 문제 해결: User ID로 중복 체크
	 */
	@Query("SELECT COUNT(o) > 0 FROM Owner o WHERE o.businessNumber = :businessNumber AND o.user.userId != :userId")
	boolean existsByBusinessNumberAndUserIdNot(@Param("businessNumber") String businessNumber, @Param("userId") UUID userId);

	// ===============================
	// 사업자번호 검색 메서드들
	// ===============================

	@Query("SELECT o FROM Owner o WHERE o.businessNumber LIKE %:businessNumber%")
	Page<Owner> findByBusinessNumberContaining(@Param("businessNumber") String businessNumber, Pageable pageable);

	@Query("SELECT o FROM Owner o WHERE o.businessNumber LIKE :prefix%")
	List<Owner> findByBusinessNumberStartingWith(@Param("prefix") String prefix);

	// ===============================
	// 날짜 관련 메서드들
	// ===============================

	@Query("SELECT o FROM Owner o WHERE o.createdAt BETWEEN :startDate AND :endDate")
	List<Owner> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	/**
	 * ✅ 최근 Owner 조회 (LocalDateTime 매개변수 버전)
	 */
	@Query("SELECT o FROM Owner o WHERE o.createdAt >= :cutoffDate ORDER BY o.createdAt DESC")
	List<Owner> findRecentOwners(@Param("cutoffDate") LocalDateTime cutoffDate);

	// ===============================
	// User 관련 메서드들
	// ===============================

	@Query("SELECT o FROM Owner o WHERE o.user.isActive = :isActive")
	Page<Owner> findByUserIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

	@Query("SELECT o FROM Owner o WHERE UPPER(o.user.name) LIKE UPPER(CONCAT('%', :name, '%'))")
	Page<Owner> findByUserNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

	@Query("SELECT o FROM Owner o WHERE UPPER(o.user.email) LIKE UPPER(CONCAT('%', :email, '%'))")
	Page<Owner> findByUserEmailContainingIgnoreCase(@Param("email") String email, Pageable pageable);

	@Query("SELECT o FROM Owner o WHERE UPPER(o.user.name) LIKE UPPER(CONCAT('%', :name, '%')) OR UPPER(o.user.email) LIKE UPPER(CONCAT('%', :email, '%'))")
	Page<Owner> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(@Param("name") String name, @Param("email") String email, Pageable pageable);

	@Query("SELECT o FROM Owner o WHERE o.user.lastLoginAt >= :cutoffDate")
	List<Owner> findByUserLastLoginAtAfter(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * ✅ 비활성 Owner 조회
	 */
	@Query("SELECT o FROM Owner o WHERE o.user.isActive = false OR o.user.lastLoginAt < :cutoffDate")
	List<Owner> findInactiveOwners(@Param("cutoffDate") LocalDateTime cutoffDate);

	// ===============================
	// 정렬 메서드들
	// ===============================

	@Query("SELECT o FROM Owner o ORDER BY o.createdAt DESC")
	Page<Owner> findAllByOrderByCreatedAtDesc(Pageable pageable);

	@Query("SELECT o FROM Owner o ORDER BY o.user.name ASC")
	Page<Owner> findAllByOrderByUserNameAsc(Pageable pageable);

	@Query("SELECT o FROM Owner o ORDER BY o.businessNumber ASC")
	Page<Owner> findAllByOrderByBusinessNumberAsc(Pageable pageable);

	// ===============================
	// JOIN FETCH 메서드들
	// ===============================

	@Query("SELECT o FROM Owner o JOIN FETCH o.user")
	Page<Owner> findAllWithUser(Pageable pageable);

	// ===============================
	// 정규식 검색 (LIKE로 대체)
	// ===============================

	/**
	 * ✅ 정규식 대신 LIKE 패턴 검색 사용 (PostgreSQL 호환)
	 */
	@Query("SELECT o FROM Owner o WHERE o.businessNumber LIKE :pattern")
	List<Owner> findByBusinessNumberRegex(@Param("pattern") String pattern);
}
