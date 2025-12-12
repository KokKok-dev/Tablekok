package com.tablekok.user_service.auth.application.service;

import com.tablekok.user_service.auth.application.dto.command.SignupCommand;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.auth.domain.service.BusinessNumberValidator;
import com.tablekok.user_service.auth.domain.service.UserValidator;
import com.tablekok.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final UserValidator userValidator;
	private final BusinessNumberValidator businessNumberValidator;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public SignupResult signup(SignupCommand command) {
		// 1. 이메일 중복 검증
		userValidator.validateEmailNotDuplicated(command.email());

		// 2. 역할 결정 + 사업자번호 검증
		UserRole role;
		if (command.hasBusinessNumber()) {
			businessNumberValidator.validate(command.businessNumber());
			role = UserRole.OWNER;
		} else {
			role = UserRole.CUSTOMER;
		}

		// 3. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(command.password());

		// 4. User 생성 및 저장
		User user = User.create(
			command.email(),
			encodedPassword,
			command.username(),
			command.phoneNumber(),
			role
		);
		User savedUser = userRepository.save(user);

		// 5. OWNER인 경우 Owner 엔티티도 생성
		if (role == UserRole.OWNER) {
			Owner owner = Owner.create(savedUser.getUserId(), command.businessNumber());
			ownerRepository.save(owner);
		}

		// 6. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			savedUser.getUserId(),
			savedUser.getRole().name()
		);

		// 7. 결과 반환
		return new SignupResult(
			savedUser.getUserId(),
			savedUser.getEmail(),
			savedUser.getUsername(),
			savedUser.getRole().name(),
			accessToken
		);
	}
}
