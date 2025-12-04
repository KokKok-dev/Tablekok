package com.tablekok.user_service.domain.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	// ========== 기본 조회 메서드 ==========

	/**
	 * 사업자번호로 사장님 정보 조회
	 */
	Optional<Owner> findByBusinessNumber(String businessNumber);

	/**
	 * 사용자 ID로 사장님 정보 조회 (User와 함께 페치)
	 */
	@Query("SELECT o FROM Owner o JOIN FETCH o.user WHERE o.userId = :userId")
	Optional<Owner> findByUserIdWithUser(@Param("userId") UUID userId);

	// ========== 존재 여부 확인 메서드 ==========

	/**
	 * 사업자번호 중복 검증
	 */
	boolean existsByBusinessNumber(String businessNumber);

	/**
	 * 자신 제외 사업자번호 중복 검증 (정보 수정용)
	 */
	boolean existsByBusinessNumberAndUserIdNot(String businessNumber, UUID userId);

	// ========== 활성 사장님 조회 메서드 ==========

	/**
	 * 활성화된 사장님만 사업자번호로 조회
	 */
	@Query("SELECT o FROM Owner o JOIN o.user u WHERE o.businessNumber = :businessNumber " +
		"AND u.isActive = true AND u.deletedAt IS NULL")
	Optional<Owner> findActiveOwnerByBusinessNumber(@Param("businessNumber") String businessNumber);

	/**
	 * 모든 활성 사장님 목록 조회
	 */
	@Query("SELECT o FROM Owner o JOIN FETCH o.user u WHERE u.isActive = true AND u.deletedAt IS NULL " +
		"ORDER BY u.createdAt DESC")
	List<Owner> findAllActiveOwners();

	// ========== 검색 메서드 ==========

	/**
	 * 사업자번호 패턴으로 검색
	 */
	@Query("SELECT o FROM Owner o JOIN FETCH o.user u WHERE o.businessNumber LIKE %:pattern% " +
		"AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
	List<Owner> findByBusinessNumberContaining(@Param("pattern") String pattern);

	/**
	 * 사장님 이름으로 검색
	 */
	@Query("SELECT o FROM Owner o JOIN FETCH o.user u WHERE u.name LIKE %:name% " +
		"AND u.deletedAt IS NULL ORDER BY u.createdAt DESC")
	List<Owner> findByUserNameContaining(@Param("name") String name);

	// ========== 통계 조회 메서드 ==========

	/**
	 * 전체 활성 사장님 수
	 */
	@Query("SELECT COUNT(o) FROM Owner o JOIN o.user u WHERE u.isActive = true AND u.deletedAt IS NULL")
	long countActiveOwners();
}
