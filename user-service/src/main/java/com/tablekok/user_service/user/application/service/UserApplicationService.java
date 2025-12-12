package com.tablekok.user_service.user.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;
import com.tablekok.user_service.user.application.exception.UserErrorCode;
import com.tablekok.user_service.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final JwtUtil jwtUtil;

	public ProfileResult getProfile(String authorizationHeader) {
		// 1. JWT에서 userId 추출
		String token = extractToken(authorizationHeader);
		UUID userId = jwtUtil.getUserId(token);

		// 2. 사용자 조회
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

		// 3. OWNER인 경우 사업자번호 조회
		String businessNumber = null;
		if (user.getRole() == UserRole.OWNER) {
			Owner owner = ownerRepository.findByUserId(userId)
				.orElse(null);
			if (owner != null) {
				businessNumber = owner.getBusinessNumber();
			}
		}

		// 4. 결과 반환
		return new ProfileResult(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getPhoneNumber(),
			user.getRole().name(),
			businessNumber,
			user.getCreatedAt()
		);
	}

	private String extractToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		throw new AppException(UserErrorCode.INVALID_TOKEN);
	}
}
