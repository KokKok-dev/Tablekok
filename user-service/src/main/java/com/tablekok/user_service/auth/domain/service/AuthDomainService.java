package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.application.dto.command.CustomerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.OwnerSignupCommand;
import com.tablekok.user_service.auth.application.dto.command.LoginCommand;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.exception.AuthErrorCode;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.service.BusinessNumberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 인증 관련 Domain Service (Auth 전용으로 축소)
 *
 * gashine20 피드백 반영:
 * 1. UserValidator 분리 적용
 * 2. AuthErrorCode 사용으로 명확한 예외 처리
 *
 * 주요 책임:
 * 1. 인증 관련 복합 비즈니스 로직
 * 2. 회원가입 자격 검증
 * 3. 사업자번호 관련 검증 (Owner 전용)
 *
 * UserDomainService로 User 관련 로직 위임 완료
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthDomainService {

	private final UserDomainService userDomainService;  // User 로직 위임
	private final OwnerRepository ownerRepository;
	private final BusinessNumberValidator businessNumberValidator;
	private final UserValidator userValidator;  // ✅ 신규 추가

	// ========== Customer 회원가입 자격 검증 ==========

	/**
	 * Customer 회원가입 자격 검증
	 * gashine20 피드백 반영: UserValidator, AuthErrorCode 사용
	 *
	 * @param command Customer 회원가입 Command
	 * @throws AppException 검증 실패 시
	 */
	public void validateCustomerSignupEligibility(CustomerSignupCommand command) {
		log.debug("Validating customer signup eligibility for email: {}", command.email());

		// ✅ UserValidator를 통한 Domain 검증
		userValidator.validateEmail(command.email());
		userValidator.validateName(command.username());
		userValidator.validatePassword(command.password());
		userValidator.validatePhoneNumber(command.phone());

		// ✅ UserDomainService로 중복 검증 위임 (AuthErrorCode 사용)
		if (!userDomainService.isEmailAvailable(command.email())) {
			log.warn("Duplicate email registration attempt: {}", command.email());
			throw new AppException(AuthErrorCode.DUPLICATE_EMAIL);
		}

		if (!userDomainService.isPhoneNumberAvailable(command.phone())) {
			log.warn("Duplicate phone number registration attempt: {}", command.phone());
			throw new AppException(AuthErrorCode.DUPLICATE_PHONE_NUMBER);
		}

		log.debug("Customer signup eligibility validation completed for email: {}", command.email());
	}

	// ========== Owner 회원가입 자격 검증 ==========

	/**
	 * Owner 회원가입 자격 검증
	 * gashine20 피드백 반영: UserValidator, AuthErrorCode 사용
	 *
	 * @param command Owner 회원가입 Command
	 * @throws AppException 검증 실패 시
	 */
	public void validateOwnerSignupEligibility(OwnerSignupCommand command) {
		log.debug("Validating owner signup eligibility for email: {}, business number: {}",
			command.email(), command.businessNumber());

		// 1. ✅ UserValidator를 통한 기본 검증
		userValidator.validateEmail(command.email());
		userValidator.validateName(command.username());
		userValidator.validatePassword(command.password());
		userValidator.validatePhoneNumber(command.phone());

		// 2. ✅ 중복 검증 (AuthErrorCode 사용)
		if (!userDomainService.isEmailAvailable(command.email())) {
			log.warn("Duplicate email registration attempt: {}", command.email());
			throw new AppException(AuthErrorCode.DUPLICATE_EMAIL);
		}

		if (!userDomainService.isPhoneNumberAvailable(command.phone())) {
			log.warn("Duplicate phone number registration attempt: {}", command.phone());
			throw new AppException(AuthErrorCode.DUPLICATE_PHONE_NUMBER);
		}

		// 3. Owner 특화 검증
		Owner.validateBusinessNumber(command.businessNumber());
		validateBusinessNumberNotDuplicated(command.businessNumber());
		validateBusinessNumberChecksum(command.businessNumber());

		log.debug("Owner signup eligibility validation completed for email: {}", command.email());
	}

	// ========== 로그인 자격 검증 ==========

	/**
	 * 로그인 자격 검증 (모든 역할 공통)
	 * gashine20 피드백 반영: UserValidator, AuthErrorCode 사용
	 *
	 * @param command 로그인 Command
	 * @throws AppException 검증 실패 시
	 */
	public void validateLoginEligibility(LoginCommand command) {
		log.debug("Validating login eligibility for email: {}", command.email());

		// ✅ UserValidator를 통한 기본 형식 검증
		userValidator.validateEmail(command.email());
		userValidator.validatePassword(command.password());

		log.debug("Login eligibility validation completed for email: {}", command.email());
	}

	// ========== 사업자번호 관련 검증 (Owner 전용) ==========

	/**
	 * 사업자번호 중복 검증
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 이미 사용 중인 사업자번호인 경우
	 */
	public void validateBusinessNumberNotDuplicated(String businessNumber) {
		log.debug("Validating business number not duplicated: {}", businessNumber);

		String normalizedBusinessNumber = Owner.normalizeBusinessNumber(businessNumber);

		if (ownerRepository.existsByBusinessNumber(normalizedBusinessNumber)) {
			log.warn("Duplicate business number registration attempt: {}", normalizedBusinessNumber);
			throw new AppException(AuthErrorCode.DUPLICATE_BUSINESS_NUMBER);
		}

		log.debug("Business number duplication validation passed: {}", normalizedBusinessNumber);
	}

	/**
	 * 사업자번호 체크섬 검증 (외부 Validator 활용)
	 * ✅ gashine20 피드백 반영: AuthErrorCode 사용
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @throws AppException 유효하지 않은 사업자번호인 경우
	 */
	public void validateBusinessNumberChecksum(String businessNumber) {
		log.debug("Validating business number checksum: {}", businessNumber);

		String normalizedBusinessNumber = Owner.normalizeBusinessNumber(businessNumber);

		if (!businessNumberValidator.isValid(normalizedBusinessNumber)) {
			log.warn("Invalid business number checksum: {}", normalizedBusinessNumber);
			throw new AppException(AuthErrorCode.INVALID_BUSINESS_NUMBER_CHECKSUM);
		}

		log.debug("Business number checksum validation passed: {}", normalizedBusinessNumber);
	}

	/**
	 * 사업자번호 사용 가능 여부 확인
	 *
	 * @param businessNumber 확인할 사업자번호
	 * @return 사용 가능 여부
	 */
	public boolean isBusinessNumberAvailable(String businessNumber) {
		log.debug("Checking business number availability: {}", businessNumber);

		try {
			// 기본 형식 검증
			Owner.validateBusinessNumber(businessNumber);
			String normalizedBusinessNumber = Owner.normalizeBusinessNumber(businessNumber);

			// 중복 검증
			boolean isNotDuplicated = !ownerRepository.existsByBusinessNumber(normalizedBusinessNumber);

			// 체크섬 검증
			boolean hasValidChecksum = businessNumberValidator.isValid(normalizedBusinessNumber);

			boolean isAvailable = isNotDuplicated && hasValidChecksum;
			log.debug("Business number availability result: {} - available: {}",
				normalizedBusinessNumber, isAvailable);

			return isAvailable;

		} catch (Exception e) {
			log.debug("Business number format invalid: {} - {}", businessNumber, e.getMessage());
			return false;
		}
	}

	/**
	 * 본인 제외 사업자번호 중복 검증 (수정 시 사용)
	 *
	 * @param businessNumber 검증할 사업자번호
	 * @param currentUserId 현재 사용자 ID (제외할 사용자)
	 * @return 중복 여부
	 */
	public boolean isBusinessNumberDuplicateExcludingSelf(String businessNumber, java.util.UUID currentUserId) {
		log.debug("Checking business number duplicate excluding self: {}, current user: {}",
			businessNumber, currentUserId);

		String normalizedBusinessNumber = Owner.normalizeBusinessNumber(businessNumber);
		boolean isDuplicate = ownerRepository.existsByBusinessNumberAndUserIdNot(normalizedBusinessNumber, currentUserId);

		log.debug("Business number duplicate check result excluding self: {} - duplicate: {}",
			normalizedBusinessNumber, isDuplicate);

		return isDuplicate;
	}

	// ========== 통계 및 조회 메서드 (Owner 관련) ==========

	/**
	 * 사업자번호 패턴으로 Owner 검색 가능 여부
	 */
	public boolean canSearchByBusinessNumberPattern(String pattern) {
		// 최소 3자리 이상 입력했을 때만 검색 허용
		return pattern != null && pattern.trim().length() >= 3;
	}

	/**
	 * Owner 등록 통계
	 */
	public long getTotalOwnerCount() {
		return ownerRepository.count();
	}
}
