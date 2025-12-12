package com.tablekok.user_service.auth.application.service;

import com.tablekok.user_service.auth.application.dto.command.SignupCommand;
import com.tablekok.user_service.auth.application.dto.result.SignupResult;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
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
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Transactional
	public SignupResult signup(SignupCommand command) {
		UserRole role = command.hasBusinessNumber() ? UserRole.OWNER : UserRole.CUSTOMER;

		String encodedPassword = passwordEncoder.encode(command.password());

		User user = User.create(
			command.email(),
			encodedPassword,
			command.username(),
			command.phoneNumber(),
			role
		);
		User savedUser = userRepository.save(user);

		if (role == UserRole.OWNER) {
			Owner owner = Owner.create(savedUser.getUserId(), command.businessNumber());
			ownerRepository.save(owner);
		}

		String accessToken = jwtUtil.generateAccessToken(
			savedUser.getUserId(),
			savedUser.getRole().name()
		);

		return new SignupResult(
			savedUser.getUserId(),
			savedUser.getEmail(),
			savedUser.getUsername(),
			savedUser.getRole().name(),
			accessToken
		);
	}
}
