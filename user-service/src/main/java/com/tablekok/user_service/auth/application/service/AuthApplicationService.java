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
 * 3. 데이터 정규화 및 검증 로직
 * 4. JWT 토큰 생성 및 관리
 * 5. 비밀번호 암호화 및 검증
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
	 */
	@Transactional
	public SignupResult signupCustomer(CustomerSignupParam param) {
		log.info("Starting customer signup process for email: {}", param.email());

		// 1. 데이터 정규화
		String normalizedEmail = normalizeEmail(param.email());
		String normalizedPhone = normalizePhone(param.phone());

		// 2. 기본 검증
		validateRequiredFields(param);

		// 3. 중복 검증
		validateDuplicateEmail(normalizedEmail);
		validateDuplicatePhoneNumber(normalizedPhone);

		// 4. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(param.password());
		log.debug("Password encoded for customer signup");

		// 5. User 엔티티 생성 - 정규화된 데이터로 새 Param 생성
		CustomerSignupParam normalizedParam = CustomerSignupParam.builder()
			.email(normalizedEmail)
			.username(param.username())
			.password(param.password()) // 원본 비밀번호 (toEntity에서 encodedPassword 사용)
			.phone(normalizedPhone)
			.build();

		User customer = normalizedParam.toEntity(encodedPassword);
		log.debug("Created customer entity with role: {}", customer.getRole());

		// 6. DB 저장
		User savedCustomer = userRepository.save(customer);
		log.info("Successfully saved customer with ID: {}", savedCustomer.getUserId());

		// 7. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedCustomer.getUserId(),
			savedCustomer.getEmail(),
			savedCustomer.getRole().name()
		);
		log.debug("Generated JWT token for customer: {}", savedCustomer.getUserId());

		// 8. 응답 DTO 생성
		SignupResult result = SignupResult.fromCustomer(accessToken, savedCustomer);
		log.info("Customer signup completed successfully for ID: {}", savedCustomer.getUserId());

		return result;
	}

	// ========== 사장님 회원가입 ==========

	/**
	 * 사장님 회원가입
	 */
	@Transactional
	public SignupResult signupOwner(OwnerSignupParam param) {
		log.info("Starting owner signup process for email: {}", param.email());

		// 1. 데이터 정규화
		String normalizedEmail = normalizeEmail(param.email());
		String normalizedPhone = normalizePhone(param.phone());
		String normalizedBusinessNumber = normalizeBusinessNumber(param.businessNumber());

		// 2. 기본 검증
		validateRequiredFields(param);

		// 3. 중복 검증
		validateDuplicateEmail(normalizedEmail);
		validateDuplicatePhoneNumber(normalizedPhone);
		validateDuplicateBusinessNumber(normalizedBusinessNumber);

		// 4. 사업자번호 유효성 검증
		validateBusinessNumber(normalizedBusinessNumber);

		// 5. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(param.password());
		log.debug("Password encoded for owner signup");

		// 6. User 엔티티 생성 - 정규화된 데이터로 새 Param 생성
		OwnerSignupParam normalizedParam = OwnerSignupParam.builder()
			.email(normalizedEmail)
			.username(param.username())
			.password(param.password())
			.phone(normalizedPhone)
			.businessNumber(normalizedBusinessNumber)
			.build();

		User ownerUser = normalizedParam.toUserEntity(encodedPassword);
		log.debug("Created owner user entity with role: {}", ownerUser.getRole());

		// 7. User 저장
		User savedOwnerUser = userRepository.save(ownerUser);
		log.info("Successfully saved owner user with ID: {}", savedOwnerUser.getUserId());

		// 8. Owner 엔티티 생성 및 저장
		Owner owner = normalizedParam.toOwnerEntity(savedOwnerUser);
		Owner savedOwner = ownerRepository.save(owner);
		log.info("Successfully saved owner with business number: {}",
			businessNumberValidator.mask(savedOwner.getBusinessNumber()));

		// 9. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedOwnerUser.getUserId(),
			savedOwnerUser.getEmail(),
			savedOwnerUser.getRole().name()
		);
		log.debug("Generated JWT token for owner: {}", savedOwnerUser.getUserId());

		// 10. 응답 DTO 생성
		SignupResult result = SignupResult.fromOwner(accessToken, savedOwnerUser);
		log.info("Owner signup completed successfully for ID: {}", savedOwnerUser.getUserId());

		return result;
	}

	// ========== 로그인 ==========

	/**
	 * 로그인 (모든 역할 공통)
	 */
	@Transactional
	public LoginResult login(LoginParam param) {
		log.info("Starting login process for email: {}", param.email());

		// 1. 이메일 정규화
		String normalizedEmail = normalizeEmail(param.email());

		// 2. 기본 검증
		validateLoginFields(param);

		// 3. 이메일로 사용자 조회
		User user = userRepository.findByEmail(normalizedEmail)
			.orElseThrow(() -> {
				log.warn("Login failed - user not found for email: {}", normalizedEmail);
				return new RuntimeException("가입되지 않은 이메일입니다.");
			});

		log.debug("Found user with ID: {} for login", user.getUserId());

		// 4. 계정 상태 확인
		if (!user.isAccountActive()) {
			log.warn("Login failed - account is inactive for user: {}", user.getUserId());
			throw new RuntimeException("비활성화된 계정입니다. 관리자에게 문의하세요.");
		}

		// 5. 비밀번호 검증
		if (!passwordEncoder.matches(param.password(), user.getPassword())) {
			log.warn("Login failed - password mismatch for user: {}", user.getUserId());
			throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		log.debug("Password verification successful for user: {}", user.getUserId());

		// 6. 로그인 정보 업데이트
		user.updateLoginInfo();
		User savedUser = userRepository.save(user);
		log.info("Login info updated for user: {} (login count: {})",
			savedUser.getUserId(), savedUser.getLoginCount());

		// 7. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedUser.getUserId(),
			savedUser.getEmail(),
			savedUser.getRole().name()
		);
		log.debug("Generated JWT token for user: {}", savedUser.getUserId());

		// 8. 응답 DTO 생성
		LoginResult result = LoginResult.from(accessToken, savedUser);
		log.info("Login completed successfully for user: {} with role: {}",
			savedUser.getUserId(), savedUser.getRole());

		return result;
	}

	// ========== 데이터 정규화 메서드들 ==========

	/**
	 * 이메일 정규화 (소문자 변환 + 공백 제거)
	 */
	private String normalizeEmail(String email) {
		return email != null ? email.toLowerCase().trim() : null;
	}

	/**
	 * 휴대폰번호 정규화 (하이픈 제거)
	 */
	private String normalizePhone(String phone) {
		return phone != null ? phone.replaceAll("-", "") : null;
	}

	/**
	 * 사업자번호 정규화 (하이픈 제거)
	 */
	private String normalizeBusinessNumber(String businessNumber) {
		return businessNumber != null ? businessNumber.replaceAll("-", "") : null;
	}

	// ========== 기본 검증 메서드들 ==========

	/**
	 * Customer 회원가입 필수 필드 검증
	 */
	private void validateRequiredFields(CustomerSignupParam param) {
		if (param.email() == null || param.email().trim().isEmpty()) {
			throw new RuntimeException("이메일은 필수 입력 값입니다.");
		}
		if (param.username() == null || param.username().trim().isEmpty()) {
			throw new RuntimeException("이름은 필수 입력 값입니다.");
		}
		if (param.password() == null || param.password().trim().isEmpty()) {
			throw new RuntimeException("비밀번호는 필수 입력 값입니다.");
		}
		if (param.phone() == null || param.phone().trim().isEmpty()) {
			throw new RuntimeException("휴대폰번호는 필수 입력 값입니다.");
		}
	}

	/**
	 * Owner 회원가입 필수 필드 검증
	 */
	private void validateRequiredFields(OwnerSignupParam param) {
		validateRequiredFields(CustomerSignupParam.builder()
			.email(param.email())
			.username(param.username())
			.password(param.password())
			.phone(param.phone())
			.build());

		if (param.businessNumber() == null || param.businessNumber().trim().isEmpty()) {
			throw new RuntimeException("사업자번호는 필수 입력 값입니다.");
		}
	}

	/**
	 * 로그인 필수 필드 검증
	 */
	private void validateLoginFields(LoginParam param) {
		if (param.email() == null || param.email().trim().isEmpty()) {
			throw new RuntimeException("이메일은 필수 입력 값입니다.");
		}
		if (param.password() == null || param.password().trim().isEmpty()) {
			throw new RuntimeException("비밀번호는 필수 입력 값입니다.");
		}
	}

	// ========== 중복 검증 메서드들 ==========

	private void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			log.warn("Duplicate email validation failed: {}", email);
			throw new RuntimeException("이미 가입된 이메일입니다.");
		}
		log.debug("Email duplication check passed: {}", email);
	}

	private void validateDuplicatePhoneNumber(String phoneNumber) {
		if (userRepository.existsByPhoneNumber(phoneNumber)) {
			log.warn("Duplicate phone number validation failed: {}", phoneNumber);
			throw new RuntimeException("이미 가입된 휴대폰번호입니다.");
		}
		log.debug("Phone number duplication check passed: {}", phoneNumber);
	}

	private void validateDuplicateBusinessNumber(String businessNumber) {
		if (ownerRepository.existsByBusinessNumber(businessNumber)) {
			log.warn("Duplicate business number validation failed: {}",
				businessNumberValidator.mask(businessNumber));
			throw new RuntimeException("이미 등록된 사업자번호입니다.");
		}
		log.debug("Business number duplication check passed");
	}

	private void validateBusinessNumber(String businessNumber) {
		if (!businessNumberValidator.isValid(businessNumber)) {
			log.warn("Business number validation failed for number: {}",
				businessNumberValidator.mask(businessNumber));
			throw new RuntimeException("유효하지 않은 사업자번호입니다.");
		}
		log.debug("Business number validation passed");
	}

	// ========== 조회 메서드들 (기존과 동일) ==========
	// findUserByEmail, findUserById 등...
}
