// auth/domain/repository/OwnerRepository.java
package com.tablekok.user_service.auth.domain.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Owner 도메인 Repository 인터페이스
 * 사장님 정보 관련 순수한 도메인 인터페이스
 * 구체적인 구현은 Infrastructure 계층에서 담당
 */
public interface OwnerRepository {

	// ========== 기본 CRUD 메서드 ==========

	/**
	 * Owner 저장
	 */
	Owner save(Owner owner);

	/**
	 * User ID로 Owner 조회
	 */
	Optional<Owner> findById(UUID userId);

	/**
	 * User 엔티티로 Owner 조회
	 */
	Optional<Owner> findByUser(User user);

	/**
	 * 모든 Owner 조회 (페이징)
	 */
	Page<Owner> findAll(Pageable pageable);

	/**
	 * Owner 삭제
	 */
	void delete(Owner owner);

	/**
	 * User ID로 Owner 삭제
	 */
	void deleteById(UUID userId);

	// ========== 사업자번호 기반 조회 메서드 ==========

	/**
	 * 사업자번호로 Owner 조회
	 */
	Optional<Owner> findByBusinessNumber(String businessNumber);

	/**
	 * 사업자번호 존재 여부 확인
	 */
	boolean existsByBusinessNumber(String businessNumber);

	/**
	 * 특정 사업자번호를 제외한 중복 확인 (수정 시 사용)
	 */
	boolean existsByBusinessNumberAndUserIdNot(String businessNumber, UUID userId);

	// ========== 사업자번호 패턴 검색 메서드 ==========

	/**
	 * 사업자번호 부분 검색 (관리자용)
	 */
	Page<Owner> findByBusinessNumberContaining(String businessNumberPart, Pageable pageable);

	/**
	 * 사업자번호 시작 패턴으로 검색
	 */
	List<Owner> findByBusinessNumberStartingWith(String prefix);

	// ========== 통계 및 집계 메서드 ==========

	/**
	 * 전체 Owner 수
	 */
	long count();

	/**
	 * 특정 기간 내 등록된 Owner 조회
	 */
	List<Owner> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

	/**
	 * 최근 등록된 Owner 조회 (일 단위)
	 */
	List<Owner> findRecentOwners(int days);

	// ========== User 연관관계 기반 조회 메서드 ==========

	/**
	 * 활성 상태인 Owner 조회 (User의 isActive 기준)
	 */
	Page<Owner> findByUserIsActive(Boolean isActive, Pageable pageable);

	/**
	 * Owner명(User의 name)으로 검색
	 */
	Page<Owner> findByUserNameContainingIgnoreCase(String ownerName, Pageable pageable);

	/**
	 * Owner 이메일(User의 email)로 검색
	 */
	Page<Owner> findByUserEmailContainingIgnoreCase(String email, Pageable pageable);

	/**
	 * Owner명 또는 이메일로 통합 검색
	 */
	Page<Owner> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
		String name, String email, Pageable pageable);

	// ========== 정렬 메서드 ==========

	/**
	 * 등록일 기준 최신순 Owner 조회
	 */
	Page<Owner> findAllByOrderByCreatedAtDesc(Pageable pageable);

	/**
	 * Owner명(User의 name) 기준 정렬
	 */
	Page<Owner> findAllByOrderByUserNameAsc(Pageable pageable);

	/**
	 * 사업자번호 기준 정렬
	 */
	Page<Owner> findAllByOrderByBusinessNumberAsc(Pageable pageable);

	// ========== 관리자용 고급 조회 메서드 ==========

	/**
	 * 특정 User ID로 Owner 존재 여부 확인
	 */
	boolean existsById(UUID userId);

	/**
	 * 최근 로그인한 Owner들 조회 (User의 lastLoginAt 기준)
	 */
	List<Owner> findByUserLastLoginAtAfter(LocalDateTime cutoffDate);

	/**
	 * 오랫동안 로그인하지 않은 Owner들 조회
	 */
	List<Owner> findInactiveOwners(LocalDateTime cutoffDate);

	/**
	 * Owner와 User 정보를 함께 조회 (Fetch Join 최적화)
	 */
	Page<Owner> findAllWithUser(Pageable pageable);

	// ========== 검증 메서드 ==========

	/**
	 * Owner가 특정 User와 연결되어 있는지 확인
	 */
	boolean isOwnerLinkedToUser(UUID userId);

	/**
	 * 사업자번호 형식 유효성 검증용 조회
	 */
	List<Owner> findByBusinessNumberRegex(String regex);

	/**
	 * 중복 사업자번호 검증 (자신 제외)
	 */
	boolean isBusinessNumberDuplicateExcludingSelf(String businessNumber, UUID currentUserId);
}
