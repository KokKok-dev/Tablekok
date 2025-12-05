package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
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
 * Owner JPA Repository 인터페이스
 * Spring Data JPA 기술에 의존하는 Infrastructure 계층
 */
public interface OwnerJpaRepository extends JpaRepository<Owner, UUID> {

	// ========== 기본 조회 메서드 ==========

	Optional<Owner> findByUser(User user);
	Optional<Owner> findByBusinessNumber(String businessNumber);

	// ========== 존재 여부 확인 메서드 ==========

	boolean existsByBusinessNumber(String businessNumber);
	boolean existsByBusinessNumberAndUserIdNot(String businessNumber, UUID userId);

	// ========== 사업자번호 검색 메서드 ==========

	Page<Owner> findByBusinessNumberContaining(String businessNumberPart, Pageable pageable);
	List<Owner> findByBusinessNumberStartingWith(String prefix);

	// ========== 기간별 조회 메서드 ==========

	List<Owner> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	// ========== User 연관관계 기반 조회 메서드 ==========

	Page<Owner> findByUserIsActive(Boolean isActive, Pageable pageable);
	Page<Owner> findByUserNameContainingIgnoreCase(String ownerName, Pageable pageable);
	Page<Owner> findByUserEmailContainingIgnoreCase(String email, Pageable pageable);
	Page<Owner> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
		String name, String email, Pageable pageable);

	// ========== 정렬 메서드 ==========

	Page<Owner> findAllByOrderByCreatedAtDesc(Pageable pageable);
	Page<Owner> findAllByOrderByUserNameAsc(Pageable pageable);
	Page<Owner> findAllByOrderByBusinessNumberAsc(Pageable pageable);

	// ========== 커스텀 쿼리 메서드 ==========

	/**
	 * 최근 등록된 Owner 조회
	 */
	@Query("SELECT o FROM Owner o WHERE o.createdAt >= :cutoffDate ORDER BY o.createdAt DESC")
	List<Owner> findRecentOwners(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * 최근 로그인한 Owner들 조회
	 */
	@Query("SELECT o FROM Owner o WHERE o.user.lastLoginAt > :cutoffDate ORDER BY o.user.lastLoginAt DESC")
	List<Owner> findByUserLastLoginAtAfter(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * 비활성 Owner들 조회 (오랫동안 로그인하지 않은)
	 */
	@Query("SELECT o FROM Owner o WHERE o.user.lastLoginAt < :cutoffDate OR o.user.lastLoginAt IS NULL")
	List<Owner> findInactiveOwners(@Param("cutoffDate") LocalDateTime cutoffDate);

	/**
	 * Owner와 User 정보를 함께 조회 (Fetch Join 최적화)
	 */
	@Query("SELECT o FROM Owner o JOIN FETCH o.user ORDER BY o.createdAt DESC")
	Page<Owner> findAllWithUser(Pageable pageable);

	/**
	 * 사업자번호 형식 검증용 조회
	 */
	@Query("SELECT o FROM Owner o WHERE o.businessNumber REGEXP :regex")
	List<Owner> findByBusinessNumberRegex(@Param("regex") String regex);

	/**
	 * 활성 Owner만 조회 (삭제되지 않고 활성 상태)
	 */
	@Query("SELECT o FROM Owner o WHERE o.user.isActive = true AND o.deletedAt IS NULL")
	Page<Owner> findActiveOwners(Pageable pageable);

	/**
	 * 특정 기간 동안 활동한 Owner 수
	 */
	@Query("SELECT COUNT(o) FROM Owner o WHERE o.user.lastLoginAt BETWEEN :startDate AND :endDate")
	long countActiveOwnersByPeriod(@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);

	/**
	 * 사업자번호 앞자리별 통계 (지역별 분석용)
	 */
	@Query("SELECT SUBSTRING(o.businessNumber, 1, 3) as prefix, COUNT(o) " +
		"FROM Owner o GROUP BY SUBSTRING(o.businessNumber, 1, 3) ORDER BY COUNT(o) DESC")
	List<Object[]> countByBusinessNumberPrefix();
}
