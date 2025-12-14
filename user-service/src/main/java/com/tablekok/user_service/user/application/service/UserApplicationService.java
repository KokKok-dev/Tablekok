package com.tablekok.user_service.user.application.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;
import com.tablekok.user_service.user.application.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;

	public ProfileResult getProfile(UUID userId, String role) {
		// 1. 사용자 조회
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

		// 2. OWNER인 경우 사업자번호 조회
		String businessNumber = getBusinessNumberIfOwner(userId, role);

		// 3. 정적 팩토리 메서드로 결과 반환
		return ProfileResult.of(user, businessNumber);
	}

	private String getBusinessNumberIfOwner(UUID userId, String role) {
		if (!"OWNER".equals(role)) {
			return null;
		}
		return ownerRepository.findByUserId(userId)
			.map(Owner::getBusinessNumber)
			.orElse(null);
	}
}
