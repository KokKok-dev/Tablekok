package com.tablekok.user_service.auth.application.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.application.dto.command.CustomerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.OwnerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.LoginCommand;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.application.dto.result.LoginResult;
import com.tablekok.user_service.auth.application.dto.result.UserDto;
import com.tablekok.user_service.auth.application.exception.AuthErrorCode;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.auth.domain.service.AuthDomainService;
import com.tablekok.user_service.auth.domain.service.UserDomainService;
import com.tablekok.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 인증 관련 Application Service (DTO 반환으로 개선)
 *
 * ✅ gashine20 피드백 반영: Application 계층용 AuthErrorCode 사용
 *
 * 주요 책임:
 * 1. 비즈니스 플로우 오케스트레이션
 * 2. Infrastructure 계층 호출 관리
 * 3. 트랜잭션 관리
 * 4. DTO 변환 관리 (Entity 직접 노출 방지)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final AuthDomainService authDomainService;
	private final UserDomainService userDomainService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// ========== 고객 회원가입 ==========

	/**
	 * 고객 회원가입
	 * 팀 피드백: command.toEntity() 메서드 활용
	 */
	@Transactional
	public SignupResult signupCustomer(CustomerSignupCommand command) {
		log.info("Starting customer signup process for email: {}", command.email());

		// 1. Domain Service에서 자격 검증
		authDomainService.validateCustomerSignupEligibility(command);

		// 2. Infrastructure: 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(command.password());
		log.debug("Password encoded for customer signup");

		// 3. DTO → Entity 변환 (팀 피드백: command.toEntity() 활용)
		User customer = command.toEntity(encodedPassword);
		log.debug("Created customer entity with role: {}", customer.getRole());

		// 4. Infrastructure: DB 저장
		User savedCustomer = userRepository.save(customer);
		log.info("Successfully saved customer with ID: {}", savedCustomer.getUserId());

		// 5. Infrastructure: JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedCustomer.getUserId(),
			savedCustomer.getEmail(),
			savedCustomer.getRole().name()
		);
		log.debug("Generated JWT token for customer: {}", savedCustomer.getUserId());

		// 6. DTO 변환: 응답 생성
		SignupResult result = SignupResult.fromCustomer(accessToken, savedCustomer);
		log.info("Customer signup completed successfully for ID: {}", savedCustomer.getUserId());

		return result;
	}

	// ========== 사장님 회원가입 ==========

	/**
	 * 사장님 회원가입
	 * 팀 피드백: command.toUserEntity() 메서드 활용
	 */
	@Transactional
	public SignupResult signupOwner(OwnerSignupCommand command) {
		log.info("Starting owner signup process for email: {}", command.email());

		// 1. Domain Service에서 자격 검증
		authDomainService.validateOwnerSignupEligibility(command);

		// 2. Infrastructure: 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(command.password());
		log.debug("Password encoded for owner signup");

		// 3. DTO → Entity 변환 (팀 피드백: command.toUserEntity() 활용)
		User ownerUser = command.toUserEntity(encodedPassword);
		log.debug("Created owner user entity with role: {}", ownerUser.getRole());

		// 4. Infrastructure: User 저장
		User savedOwnerUser = userRepository.save(ownerUser);
		log.info("Successfully saved owner user with ID: {}", savedOwnerUser.getUserId());

		// 5. Domain Entity: Owner 생성 (양방향 연관관계 자동 설정)
		Owner owner = command.toOwnerEntity(savedOwnerUser);
		log.debug("Created owner entity with business number");

		// 6. Infrastructure: Owner 저장
		Owner savedOwner = ownerRepository.save(owner);
		log.info("Successfully saved owner");

		// 7. Infrastructure: JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedOwnerUser.getUserId(),
			savedOwnerUser.getEmail(),
			savedOwnerUser.getRole().name()
		);
		log.debug("Generated JWT token for owner: {}", savedOwnerUser.getUserId());

		// 8. DTO 변환: 응답 생성
		SignupResult result = SignupResult.fromOwner(accessToken, savedOwnerUser);
		log.info("Owner signup completed successfully for ID: {}", savedOwnerUser.getUserId());

		return result;
	}

	// ========== 로그인 ==========

	/**
	 * 로그인 (모든 역할 공통)
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 */
	@Transactional
	public LoginResult login(LoginCommand command) {
		log.info("Starting login process for email: {}", command.email());

		// 1. Domain Service: 기본 검증
		authDomainService.validateLoginEligibility(command);

		// 2. UserDomainService: 사용자 조회 (Optional 처리 자동)
		User user = userDomainService.getUserByEmail(command.email());
		log.debug("Found user with ID: {} for login", user.getUserId());

		// 3. UserDomainService: 계정 상태 검증
		userDomainService.validateAccountActive(user);

		// 4. ✅ Infrastructure: 비밀번호 검증 (AuthErrorCode 사용)
		if (!passwordEncoder.matches(command.password(), user.getPassword())) {
			log.warn("Login failed - password mismatch for user: {}", user.getUserId());
			throw new AppException(AuthErrorCode.INVALID_CREDENTIALS);
		}
		log.debug("Password verification successful for user: {}", user.getUserId());

		// 5. Domain Entity: 로그인 정보 업데이트
		user.updateLoginInfo();
		User savedUser = userRepository.save(user);
		log.info("Login info updated for user: {} (login count: {})",
			savedUser.getUserId(), savedUser.getLoginCount());

		// 6. Infrastructure: JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedUser.getUserId(),
			savedUser.getEmail(),
			savedUser.getRole().name()
		);
		log.debug("Generated JWT token for user: {}", savedUser.getUserId());

		// 7. DTO 변환: 응답 생성
		LoginResult result = LoginResult.from(accessToken, savedUser);
		log.info("Login completed successfully for user: {} with role: {}",
			savedUser.getUserId(), savedUser.getRole());

		return result;
	}

	// ========== 조회 메서드들 (DTO 반환으로 변경) ==========

	/**
	 * 이메일로 사용자 조회 (DTO 반환)
	 * 팀 피드백: Controller에서 Entity 직접 참조 방지
	 */
	public UserDto findUserByEmail(String email) {
		log.debug("Finding user by email: {}", email);
		User user = userDomainService.getUserByEmail(email);
		return UserDto.from(user);
	}

	/**
	 * 사용자 ID로 조회 (DTO 반환)
	 * 팀 피드백: Controller에서 Entity 직접 참조 방지
	 */
	public UserDto findUserById(UUID userId) {
		log.debug("Finding user by ID: {}", userId);
		User user = userDomainService.getUserById(userId);
		return UserDto.from(user);
	}

	/**
	 * 사용자 ID 문자열로 조회 (DTO 반환)
	 * 팀 피드백: Controller에서 Entity 직접 참조 방지
	 */
	public UserDto findUserByIdString(String userIdStr) {
		log.debug("Finding user by ID string: {}", userIdStr);
		User user = userDomainService.getUserByIdString(userIdStr);
		return UserDto.from(user);
	}

	/**
	 * 이메일 사용 가능 여부 확인 (UserDomainService 위임)
	 */
	public boolean isEmailAvailable(String email) {
		return userDomainService.isEmailAvailable(email);
	}

	/**
	 * 휴대폰번호 사용 가능 여부 확인 (UserDomainService 위임)
	 */
	public boolean isPhoneNumberAvailable(String phoneNumber) {
		return userDomainService.isPhoneNumberAvailable(phoneNumber);
	}

	/**
	 * 사업자번호 사용 가능 여부 확인 (AuthDomainService 위임)
	 */
	public boolean isBusinessNumberAvailable(String businessNumber) {
		return authDomainService.isBusinessNumberAvailable(businessNumber);
	}

	// ========== 통계 메서드들 (UserDomainService 위임) ==========

	/**
	 * 전체 사용자 수 조회
	 */
	public long getTotalUserCount() {
		return userDomainService.getTotalUserCount();
	}

	/**
	 * 역할별 사용자 수 조회
	 */
	public long getUserCountByRole(UserRole role) {
		return userDomainService.getUserCountByRole(role);
	}

	/**
	 * 활성 사용자 수 조회
	 */
	public long getActiveUserCount() {
		return userDomainService.getActiveUserCount();
	}

	/**
	 * 최근 가입자 수 조회 (일 단위)
	 */
	public long getRecentSignupCount(int days) {
		return userDomainService.getRecentSignupCount(days);
	}
}
