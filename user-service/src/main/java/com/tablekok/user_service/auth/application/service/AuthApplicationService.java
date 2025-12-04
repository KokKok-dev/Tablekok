// auth/application/service/AuthApplicationService.java
package com.tablekok.user_service.auth.application.service;

import com.tablekok.user_service.auth.application.dto.*;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.enums.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.auth.domain.validator.BusinessNumberValidator;
import com.tablekok.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 인증 관련 Application Service
 *
 * 주요 책임:
 * 1. 회원가입 비즈니스 로직 (Customer/Owner)
 * 2. 로그인 비즈니스 로직 (모든 역할)
 * 3. 중복 검증 (이메일, 휴대폰번호, 사업자번호)
 * 4. JWT 토큰 생성 및 관리
 * 5. 비밀번호 암호화 및 검증
 *
 * Phase 1에서 구현한 JwtUtil, PasswordEncoder 활용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final BusinessNumberValidator businessNumberValidator;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// ========== 고객 회원가입 ==========

	/**
	 * 고객 회원가입
	 *
	 * 프로세스:
	 * 1. 중복 검증 (이메일, 휴대폰번호)
	 * 2. 비밀번호 암호화 (BCrypt)
	 * 3. User 엔티티 생성 (CUSTOMER 역할)
	 * 4. DB 저장
	 * 5. JWT 토큰 생성
	 * 6. 응답 DTO 생성
	 *
	 * @param param 고객 회원가입 파라미터
	 * @return 회원가입 결과 (JWT 토큰 포함)
	 * @throws RuntimeException 중복된 이메일/휴대폰번호인 경우
	 */
	@Transactional
	public SignupResult signupCustomer(CustomerSignupParam param) {
		log.info("Starting customer signup process for email: {}", param.getNormalizedEmail());

		// 1. 중복 검증
		validateDuplicateEmail(param.getNormalizedEmail());
		validateDuplicatePhoneNumber(param.getNormalizedPhone());

		// 2. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(param.password());
		log.debug("Password encoded for customer signup");

		// 3. User 엔티티 생성 (CUSTOMER 역할)
		User customer = param.toEntity(encodedPassword);
		log.debug("Created customer entity with role: {}", customer.getRole());

		// 4. DB 저장
		User savedCustomer = userRepository.save(customer);
		log.info("Successfully saved customer with ID: {}", savedCustomer.getUserId());

		// 5. JWT 토큰 생성 (실제 JwtUtil 메서드 사용)
		String accessToken = jwtUtil.generateAccessToken(
			savedCustomer.getUserId(),        // UUID userId
			savedCustomer.getEmail(),         // String email
			savedCustomer.getRole().name()    // String role (CUSTOMER)
		);
		log.debug("Generated JWT token for customer: {}", savedCustomer.getUserId());

		// 6. 응답 DTO 생성
		SignupResult result = SignupResult.fromCustomer(accessToken, savedCustomer);
		log.info("Customer signup completed successfully for ID: {}", savedCustomer.getUserId());

		return result;
	}

	// ========== 사장님 회원가입 ==========

	/**
	 * 사장님 회원가입
	 *
	 * 프로세스:
	 * 1. 중복 검증 (이메일, 휴대폰번호, 사업자번호)
	 * 2. 사업자번호 유효성 검증 (체크섬 알고리즘)
	 * 3. 비밀번호 암호화 (BCrypt)
	 * 4. User 엔티티 생성 (OWNER 역할)
	 * 5. Owner 엔티티 생성 (양방향 연관관계)
	 * 6. DB 저장 (User → Owner 순서)
	 * 7. JWT 토큰 생성
	 * 8. 응답 DTO 생성
	 *
	 * @param param 사장님 회원가입 파라미터
	 * @return 회원가입 결과 (JWT 토큰 포함)
	 * @throws RuntimeException 중복이거나 유효하지 않은 정보인 경우
	 */
	@Transactional
	public SignupResult signupOwner(OwnerSignupParam param) {
		log.info("Starting owner signup process for email: {}", param.getNormalizedEmail());

		// 1. 중복 검증
		validateDuplicateEmail(param.getNormalizedEmail());
		validateDuplicatePhoneNumber(param.getNormalizedPhone());
		validateDuplicateBusinessNumber(param.getNormalizedBusinessNumber());

		// 2. 사업자번호 유효성 검증
		validateBusinessNumber(param.getNormalizedBusinessNumber());

		// 3. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(param.password());
		log.debug("Password encoded for owner signup");

		// 4. User 엔티티 생성 (OWNER 역할)
		User ownerUser = param.toUserEntity(encodedPassword);
		log.debug("Created owner user entity with role: {}", ownerUser.getRole());

		// 5. User 저장 (먼저 저장해서 ID 생성)
		User savedOwnerUser = userRepository.save(ownerUser);
		log.info("Successfully saved owner user with ID: {}", savedOwnerUser.getUserId());

		// 6. Owner 엔티티 생성 (양방향 연관관계)
		Owner owner = param.toOwnerEntity(savedOwnerUser);
		log.debug("Created owner entity with business number: {}",
			businessNumberValidator.mask(owner.getBusinessNumber()));

		// 7. Owner 저장
		Owner savedOwner = ownerRepository.save(owner);
		log.info("Successfully saved owner with business number: {}",
			businessNumberValidator.mask(savedOwner.getBusinessNumber()));

		// 8. JWT 토큰 생성 (실제 JwtUtil 메서드 사용)
		String accessToken = jwtUtil.generateAccessToken(
			savedOwnerUser.getUserId(),       // UUID userId
			savedOwnerUser.getEmail(),        // String email
			savedOwnerUser.getRole().name()   // String role (OWNER)
		);
		log.debug("Generated JWT token for owner: {}", savedOwnerUser.getUserId());

		// 9. 응답 DTO 생성
		SignupResult result = SignupResult.fromOwner(accessToken, savedOwnerUser);
		log.info("Owner signup completed successfully for ID: {}", savedOwnerUser.getUserId());

		return result;
	}

	// ========== 로그인 ==========

	/**
	 * 로그인 (모든 역할 공통)
	 *
	 * 프로세스:
	 * 1. 이메일로 사용자 조회
	 * 2. 계정 상태 확인 (활성/비활성)
	 * 3. 비밀번호 검증 (BCrypt)
	 * 4. 로그인 정보 업데이트 (lastLoginAt, loginCount)
	 * 5. JWT 토큰 생성
	 * 6. 응답 DTO 생성
	 *
	 * @param param 로그인 파라미터
	 * @return 로그인 결과 (JWT 토큰 포함)
	 * @throws RuntimeException 사용자 없음, 비밀번호 불일치, 계정 비활성 등
	 */
	@Transactional
	public LoginResult login(LoginParam param) {
		log.info("Starting login process for email: {}", param.getNormalizedEmail());

		// 1. 이메일로 사용자 조회
		User user = userRepository.findByEmail(param.getNormalizedEmail())
			.orElseThrow(() -> {
				log.warn("Login failed - user not found for email: {}", param.getNormalizedEmail());
				return new RuntimeException("가입되지 않은 이메일입니다.");
			});

		log.debug("Found user with ID: {} for login", user.getUserId());

		// 2. 계정 상태 확인
		if (!user.isAccountActive()) {
			log.warn("Login failed - account is inactive for user: {}", user.getUserId());
			throw new RuntimeException("비활성화된 계정입니다. 관리자에게 문의하세요.");
		}

		// 3. 비밀번호 검증
		if (!passwordEncoder.matches(param.password(), user.getPassword())) {
			log.warn("Login failed - password mismatch for user: {}", user.getUserId());
			throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		log.debug("Password verification successful for user: {}", user.getUserId());

		// 4. 로그인 정보 업데이트
		user.updateLoginInfo();
		User savedUser = userRepository.save(user);
		log.info("Login info updated for user: {} (login count: {})",
			savedUser.getUserId(), savedUser.getLoginCount());

		// 5. JWT 토큰 생성 (실제 JwtUtil 메서드 사용)
		String accessToken = jwtUtil.generateAccessToken(
			savedUser.getUserId(),            // UUID userId
			savedUser.getEmail(),             // String email
			savedUser.getRole().name()        // String role
		);
		log.debug("Generated JWT token for user: {}", savedUser.getUserId());

		// 6. 응답 DTO 생성 (역할에 따라 자동 선택)
		LoginResult result = LoginResult.from(accessToken, savedUser);
		log.info("Login completed successfully for user: {} with role: {}",
			savedUser.getUserId(), savedUser.getRole());

		return result;
	}

	// ========== 중복 검증 메서드들 ==========

	/**
	 * 이메일 중복 검증
	 *
	 * @param email 정규화된 이메일
	 * @throws RuntimeException 이미 가입된 이메일인 경우
	 */
	private void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			log.warn("Duplicate email validation failed: {}", email);
			throw new RuntimeException("이미 가입된 이메일입니다.");
		}
		log.debug("Email duplication check passed: {}", email);
	}

	/**
	 * 휴대폰번호 중복 검증
	 *
	 * @param phoneNumber 정규화된 휴대폰번호
	 * @throws RuntimeException 이미 가입된 휴대폰번호인 경우
	 */
	private void validateDuplicatePhoneNumber(String phoneNumber) {
		if (userRepository.existsByPhoneNumber(phoneNumber)) {
			log.warn("Duplicate phone number validation failed: {}", phoneNumber);
			throw new RuntimeException("이미 가입된 휴대폰번호입니다.");
		}
		log.debug("Phone number duplication check passed: {}", phoneNumber);
	}

	/**
	 * 사업자번호 중복 검증
	 *
	 * @param businessNumber 정규화된 사업자번호
	 * @throws RuntimeException 이미 등록된 사업자번호인 경우
	 */
	private void validateDuplicateBusinessNumber(String businessNumber) {
		if (ownerRepository.existsByBusinessNumber(businessNumber)) {
			log.warn("Duplicate business number validation failed: {}",
				businessNumberValidator.mask(businessNumber));
			throw new RuntimeException("이미 등록된 사업자번호입니다.");
		}
		log.debug("Business number duplication check passed");
	}

	/**
	 * 사업자번호 유효성 검증
	 * BusinessNumberValidator의 체크섬 알고리즘 활용
	 *
	 * @param businessNumber 정규화된 사업자번호
	 * @throws RuntimeException 유효하지 않은 사업자번호인 경우
	 */
	private void validateBusinessNumber(String businessNumber) {
		if (!businessNumberValidator.isValid(businessNumber)) {
			log.warn("Business number validation failed for number: {}",
				businessNumberValidator.mask(businessNumber));
			throw new RuntimeException("유효하지 않은 사업자번호입니다.");
		}
		log.debug("Business number validation passed");
	}

	// ========== 조회 메서드들 ==========

	/**
	 * 이메일로 사용자 조회 (공개 API용)
	 *
	 * @param email 이메일 주소
	 * @return 사용자 엔티티 (Optional)
	 */
	public Optional<User> findUserByEmail(String email) {
		log.debug("Finding user by email: {}", email);
		return userRepository.findByEmail(email);
	}

	/**
	 * 사용자 ID로 조회 (공개 API용)
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 엔티티 (Optional)
	 */
	public Optional<User> findUserById(UUID userId) {
		log.debug("Finding user by ID: {}", userId);
		return userRepository.findById(userId);
	}

	/**
	 * 사용자 ID 문자열로 조회
	 *
	 * @param userIdStr 사용자 ID 문자열
	 * @return 사용자 엔티티 (Optional)
	 */
	public Optional<User> findUserByIdString(String userIdStr) {
		log.debug("Finding user by ID string: {}", userIdStr);
		try {
			UUID userId = UUID.fromString(userIdStr);
			return userRepository.findById(userId);
		} catch (IllegalArgumentException e) {
			log.warn("Invalid UUID format for user ID: {}", userIdStr);
			return Optional.empty();
		}
	}

	/**
	 * 이메일 사용 가능 여부 확인
	 *
	 * @param email 확인할 이메일
	 * @return 사용 가능하면 true
	 */
	public boolean isEmailAvailable(String email) {
		String normalizedEmail = email != null ? email.toLowerCase().trim() : null;
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
		String normalizedPhone = phoneNumber != null ? phoneNumber.replaceAll("-", "") : null;
		boolean available = !userRepository.existsByPhoneNumber(normalizedPhone);
		log.debug("Phone number availability check: {}", available);
		return available;
	}

	/**
	 * 사업자번호 사용 가능 여부 확인
	 *
	 * @param businessNumber 확인할 사업자번호
	 * @return 사용 가능하면 true
	 */
	public boolean isBusinessNumberAvailable(String businessNumber) {
		String normalizedBusiness = businessNumber != null ? businessNumber.replaceAll("-", "") : null;
		boolean available = !ownerRepository.existsByBusinessNumber(normalizedBusiness);
		log.debug("Business number availability check: {}", available);
		return available;
	}

	// ========== 통계 메서드들 ==========

	/**
	 * 전체 사용자 수 조회
	 *
	 * @return 전체 사용자 수
	 */
	public long getTotalUserCount() {
		long count = userRepository.count();
		log.debug("Total user count: {}", count);
		return count;
	}

	/**
	 * 역할별 사용자 수 조회
	 *
	 * @param role 사용자 역할
	 * @return 해당 역할의 사용자 수
	 */
	public long getUserCountByRole(UserRole role) {
		long count = userRepository.countByRole(role);
		log.debug("User count for role {}: {}", role, count);
		return count;
	}

	/**
	 * 활성 사용자 수 조회
	 *
	 * @return 활성 사용자 수
	 */
	public long getActiveUserCount() {
		long count = userRepository.countByIsActive(true);
		log.debug("Active user count: {}", count);
		return count;
	}

	/**
	 * 최근 가입자 수 조회 (일 단위)
	 *
	 * @param days 조회할 일수
	 * @return 최근 가입자 수
	 */
	public long getRecentSignupCount(int days) {
		LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
		long count = userRepository.findByCreatedAtBetween(cutoffDate, LocalDateTime.now()).size();
		log.debug("Recent signup count for {} days: {}", days, count);
		return count;
	}
}
