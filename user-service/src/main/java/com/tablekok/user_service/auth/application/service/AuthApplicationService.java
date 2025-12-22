package com.tablekok.user_service.auth.application.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.application.dto.command.LoginCommand;
import com.tablekok.user_service.auth.application.dto.command.SignupCommand;
import com.tablekok.user_service.auth.application.dto.result.LoginResult;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.application.exception.AuthErrorCode;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.auth.domain.service.BusinessNumberValidator;
import com.tablekok.user_service.auth.domain.service.PasswordValidator;
import com.tablekok.user_service.auth.domain.service.UserValidator;
import com.tablekok.user_service.auth.infrastructure.redis.RedisAuthService;
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
	private final PasswordValidator passwordValidator;
	private final RedisAuthService redisAuthService;

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
	public LoginResult login(LoginCommand command) {
		// 1. 이메일로 사용자 조회
		User user = userRepository.findByEmail(command.email())
			.orElseThrow(() -> new AppException(AuthErrorCode.LOGIN_FAILED));

		// 2. 비밀번호 검증
		passwordValidator.validatePassword(command.password(), user.getPassword());

		// 3. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(
			user.getUserId(),
			user.getRole().name()
		);

		// 4. Refresh Token 생성 + Redis 저장
		String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
		redisAuthService.saveRefreshToken(user.getUserId(), refreshToken);

		// 5. 결과 반환
		return new LoginResult(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getRole().name(),
			accessToken,
			refreshToken
		);
	}
}
