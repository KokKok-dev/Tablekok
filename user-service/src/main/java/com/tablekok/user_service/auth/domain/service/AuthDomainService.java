// auth/domain/service/AuthDomainService.java
package com.tablekok.user_service.auth.domain.service;

import com.tablekok.user_service.auth.application.dto.CustomerSignupParam;
import com.tablekok.user_service.auth.application.dto.LoginParam;
import com.tablekok.user_service.auth.application.dto.OwnerSignupParam;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.validator.BusinessNumberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 인증 관련 Domain Service (Auth 전용으로 축소)
 *
 * 주요 책임:
 * 1. 인증 관련 복합 비즈니스 로직
 * 2. Owner 관련 검증 (사업자번호)
 * 3. 인증 자격 검증 통합
 *
 * User 관련 로직은 UserDomainService로 분리됨
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthDomainService {

	private final UserDomainService userDomainService;  // User 로직 위임
	private final OwnerRepository ownerRepository;
	private final BusinessNumberValidator businessNumberValidator;

	// ========== Customer 회원가입 자격 검증 ==========

	/**
	 * Customer 회원가입 자격 검증
	 *
	 * @param param Customer 회원가입 파라미터
	 * @throws IllegalArgumentException 회원가입 자격이 없는 경우
	 */
	public void validateCustomerSignupEligibility(CustomerSignupParam param) {
		log.debug("Starting customer signup eligibility validation for email: {}", param.email());

		// 1. Domain Entity 검증 적용
		User.validateEmail(param.email());
		User.validateName(param.username());
		User.validatePassword(param.password());
		User.validatePhoneNumber(param.phone());

		// 2. UserDomainService로 중복 검증 위임
		userDomainService.validateEmailNotDuplicated(param.email());
		userDomainService.validatePhoneNumberNotDuplicated(param.phone());

		log.debug("Customer signup eligibility validation passed");
	}

	// ========== Owner 회원가입 자격 검증 ==========

	/**
	 * Owner 회원가입 자격 검증
	 * Customer 검증 + Owner 특화 검증
	 *
	 * @param param Owner 회원가입 파라미터
	 * @throws IllegalArgumentException 회원가입 자격이 없는 경우
	 */
	public void validateOwnerSignupEligibility(OwnerSignupParam param) {
		log.debug("Starting owner signup eligibility validation for email: {}", param.email());

		// 1. Customer 기본 검증 재사용
		CustomerSignupParam customerParam = CustomerSignupParam.builder()
			.email(param.email())
			.username(param.username())
			.password(param.password())
			.phone(param.phone())
			.build();
		validateCustomerSignupEligibility(customerParam);

		// 2. Owner 특화 검증
		String normalizedBusinessNumber = com.tablekok.user_service.auth.domain.entity.Owner
			.normalizeBusinessNumber(param.businessNumber());

		// Domain Entity 기본 검증
		com.tablekok.user_service.auth.domain.entity.Owner.validateBusinessNumber(param.businessNumber());

		// 사업자번호 중복 검증
		validateBusinessNumberNotDuplicated(normalizedBusinessNumber);

		// 사업자번호 체크섬 검증
		validateBusinessNumberChecksum(normalizedBusinessNumber);

		log.debug("Owner signup eligibility validation passed");
	}

	// ========== 로그인 자격 검증 ==========

	/**
	 * 로그인 자격 검증
	 *
	 * @param param 로그인 파라미터
	 * @throws IllegalArgumentException 로그인 자격이 없는 경우
	 */
	public void validateLoginEligibility(LoginParam param) {
		log.debug("Starting login eligibility validation for email: {}", param.email());

		// Domain Entity 검증 적용
		User.validateEmail(param.email());

		if (param.password() == null || param.password().trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수 입력 값입니다.");
		}

		log.debug("Login eligibility validation passed");
	}

	// ========== 사업자번호 관련 검증 (Owner 전용) ==========

	/**
	 * 사업자번호 중복 검증
	 */
	public void validateBusinessNumberNotDuplicated(String normalizedBusinessNumber) {
		if (ownerRepository.existsByBusinessNumber(normalizedBusinessNumber)) {
			log.warn("Business number duplication validation failed: {}",
				businessNumberValidator.mask(normalizedBusinessNumber));
			throw new IllegalArgumentException("이미 등록된 사업자번호입니다.");
		}
		log.debug("Business number duplication check passed");
	}

	/**
	 * 사업자번호 체크섬 검증
	 */
	public void validateBusinessNumberChecksum(String normalizedBusinessNumber) {
		if (!businessNumberValidator.isValid(normalizedBusinessNumber)) {
			log.warn("Business number checksum validation failed for number: {}",
				businessNumberValidator.mask(normalizedBusinessNumber));
			throw new IllegalArgumentException("유효하지 않은 사업자번호입니다.");
		}
		log.debug("Business number checksum validation passed");
	}

	/**
	 * 사업자번호 사용 가능 여부 확인
	 */
	public boolean isBusinessNumberAvailable(String businessNumber) {
		String normalizedBusiness = com.tablekok.user_service.auth.domain.entity.Owner
			.normalizeBusinessNumber(businessNumber);
		boolean available = !ownerRepository.existsByBusinessNumber(normalizedBusiness);
		log.debug("Business number availability check: {}", available);
		return available;
	}
}
