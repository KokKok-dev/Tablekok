package com.tablekok.user_service.auth.infrastructure.repository;

import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
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
 * OwnerRepository 구현체
 * Domain Repository 인터페이스를 Infrastructure에서 구현
 * 피드백: Repository layer separation
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OwnerRepositoryAdapter implements OwnerRepository {

	private final com.tablekok.user_service.auth.infrastructure.repository.OwnerJpaRepository ownerJpaRepository;

	// ========== 기본 CRUD 메서드 ==========

	@Override
	public Owner save(Owner owner) {
		log.debug("Saving owner with business number: {}", owner.getBusinessNumber());
		return ownerJpaRepository.save(owner);
	}

	@Override
	public Optional<Owner> findById(UUID userId) {
		log.debug("Finding owner by user ID: {}", userId);
		return ownerJpaRepository.findById(userId);
	}

	@Override
	public Optional<Owner> findByUser(User user) {
		log.debug("Finding owner by user: {}", user.getUserId());
		return ownerJpaRepository.findByUser(user);
	}

	@Override
	public Page<Owner> findAll(Pageable pageable) {
		log.debug("Finding all owners with pageable: {}", pageable);
		return ownerJpaRepository.findAll(pageable);
	}

	@Override
	public void delete(Owner owner) {
		log.debug("Deleting owner with user ID: {}", owner.getUser().getUserId());
		ownerJpaRepository.delete(owner);
	}

	@Override
	public void deleteById(UUID userId) {
		log.debug("Deleting owner by user ID: {}", userId);
		ownerJpaRepository.deleteById(userId);
	}

	// ========== 사업자번호 기반 조회 메서드 ==========

	@Override
	public Optional<Owner> findByBusinessNumber(String businessNumber) {
		log.debug("Finding owner by business number: {}", businessNumber);
		return ownerJpaRepository.findByBusinessNumber(businessNumber);
	}

	@Override
	public boolean existsByBusinessNumber(String businessNumber) {
		log.debug("Checking if business number exists: {}", businessNumber);
		return ownerJpaRepository.existsByBusinessNumber(businessNumber);
	}

	@Override
	public boolean existsByBusinessNumberAndUserIdNot(String businessNumber, UUID userId) {
		log.debug("Checking if business number exists excluding user ID: {}, business number: {}", userId, businessNumber);
		return ownerJpaRepository.existsByBusinessNumberAndUserIdNot(businessNumber, userId);
	}

	// ========== 사업자번호 패턴 검색 메서드 ==========

	@Override
	public Page<Owner> findByBusinessNumberContaining(String businessNumberPart, Pageable pageable) {
		log.debug("Searching owners by business number part: {} with pageable: {}", businessNumberPart, pageable);
		return ownerJpaRepository.findByBusinessNumberContaining(businessNumberPart, pageable);
	}

	@Override
	public List<Owner> findByBusinessNumberStartingWith(String prefix) {
		log.debug("Finding owners by business number prefix: {}", prefix);
		return ownerJpaRepository.findByBusinessNumberStartingWith(prefix);
	}

	// ========== 통계 및 집계 메서드 ==========

	@Override
	public long count() {
		log.debug("Counting all owners");
		return ownerJpaRepository.count();
	}

	@Override
	public List<Owner> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
		log.debug("Finding owners created between: {} and {}", startDate, endDate);
		return ownerJpaRepository.findByCreatedAtBetween(startDate, endDate);
	}

	@Override
	public List<Owner> findRecentOwners(int days) {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
		log.debug("Finding recent owners within {} days, cutoff: {}", days, cutoffDate);
		return ownerJpaRepository.findRecentOwners(cutoffDate);
	}

	// ========== User 연관관계 기반 조회 메서드 ==========

	@Override
	public Page<Owner> findByUserIsActive(Boolean isActive, Pageable pageable) {
		log.debug("Finding owners by user active status: {} with pageable: {}", isActive, pageable);
		return ownerJpaRepository.findByUserIsActive(isActive, pageable);
	}

	@Override
	public Page<Owner> findByUserNameContainingIgnoreCase(String ownerName, Pageable pageable) {
		log.debug("Searching owners by name containing: {} with pageable: {}", ownerName, pageable);
		return ownerJpaRepository.findByUserNameContainingIgnoreCase(ownerName, pageable);
	}

	@Override
	public Page<Owner> findByUserEmailContainingIgnoreCase(String email, Pageable pageable) {
		log.debug("Searching owners by email containing: {} with pageable: {}", email, pageable);
		return ownerJpaRepository.findByUserEmailContainingIgnoreCase(email, pageable);
	}

	@Override
	public Page<Owner> findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(
		String name, String email, Pageable pageable) {
		log.debug("Searching owners by name: {} or email: {} with pageable: {}", name, email, pageable);
		return ownerJpaRepository.findByUserNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(name, email, pageable);
	}

	// ========== 정렬 메서드 ==========

	@Override
	public Page<Owner> findAllByOrderByCreatedAtDesc(Pageable pageable) {
		log.debug("Finding all owners ordered by creation date desc with pageable: {}", pageable);
		return ownerJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
	}

	@Override
	public Page<Owner> findAllByOrderByUserNameAsc(Pageable pageable) {
		log.debug("Finding all owners ordered by user name asc with pageable: {}", pageable);
		return ownerJpaRepository.findAllByOrderByUserNameAsc(pageable);
	}

	@Override
	public Page<Owner> findAllByOrderByBusinessNumberAsc(Pageable pageable) {
		log.debug("Finding all owners ordered by business number asc with pageable: {}", pageable);
		return ownerJpaRepository.findAllByOrderByBusinessNumberAsc(pageable);
	}

	// ========== 관리자용 고급 조회 메서드 ==========

	@Override
	public boolean existsById(UUID userId) {
		log.debug("Checking if owner exists by user ID: {}", userId);
		return ownerJpaRepository.existsById(userId);
	}

	@Override
	public List<Owner> findByUserLastLoginAtAfter(LocalDateTime cutoffDate) {
		log.debug("Finding owners with last login after: {}", cutoffDate);
		return ownerJpaRepository.findByUserLastLoginAtAfter(cutoffDate);
	}

	@Override
	public List<Owner> findInactiveOwners(LocalDateTime cutoffDate) {
		log.debug("Finding inactive owners with cutoff date: {}", cutoffDate);
		return ownerJpaRepository.findInactiveOwners(cutoffDate);
	}

	@Override
	public Page<Owner> findAllWithUser(Pageable pageable) {
		log.debug("Finding all owners with user fetch join, pageable: {}", pageable);
		return ownerJpaRepository.findAllWithUser(pageable);
	}

	// ========== 검증 메서드 ==========

	@Override
	public boolean isOwnerLinkedToUser(UUID userId) {
		log.debug("Checking if owner is linked to user: {}", userId);
		return ownerJpaRepository.existsById(userId);
	}

	@Override
	public List<Owner> findByBusinessNumberRegex(String regex) {
		log.debug("Finding owners by business number regex: {}", regex);
		return ownerJpaRepository.findByBusinessNumberRegex(regex);
	}

	@Override
	public boolean isBusinessNumberDuplicateExcludingSelf(String businessNumber, UUID currentUserId) {
		log.debug("Checking business number duplicate excluding self - business number: {}, current user: {}",
			businessNumber, currentUserId);
		return ownerJpaRepository.existsByBusinessNumberAndUserIdNot(businessNumber, currentUserId);
	}
}
