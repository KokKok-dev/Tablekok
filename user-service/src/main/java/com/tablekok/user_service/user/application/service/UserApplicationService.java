package com.tablekok.user_service.user.application.service;

import com.tablekok.dto.ApiResponse;
import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.entity.Owner;
import com.tablekok.user_service.auth.domain.entity.User;
import com.tablekok.user_service.auth.domain.entity.UserRole;
import com.tablekok.user_service.auth.domain.repository.OwnerRepository;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import com.tablekok.user_service.user.application.dto.command.UpdateProfileCommand;
import com.tablekok.user_service.user.application.dto.command.ChangePasswordCommand;
import com.tablekok.user_service.user.application.dto.result.ProfileResult;
import com.tablekok.user_service.user.application.dto.result.UpdateProfileResult;
import com.tablekok.user_service.user.application.dto.result.UserDetailResult;
import com.tablekok.user_service.user.application.dto.result.UserListResult;
import com.tablekok.user_service.user.application.exception.UserErrorCode;
import com.tablekok.user_service.user.presentation.dto.request.ChangePasswordRequest;
import com.tablekok.user_service.user.presentation.dto.response.ChangePasswordResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationService {

	private final UserRepository userRepository;
	private final OwnerRepository ownerRepository;
	private final PasswordEncoder passwordEncoder;

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
		if (!UserRole.OWNER.name().equals(role)) {
			return null;
		}
		return ownerRepository.findByUserId(userId)
			.map(Owner::getBusinessNumber)
			.orElse(null);
	}

	public UserListResult getAllUsers(String role, Pageable pageable) {
		// 1. MASTER 권한 확인
		if (!UserRole.MASTER.name().equals(role)) {
			throw new AppException(UserErrorCode.FORBIDDEN);
		}

		// 2. 전체 사용자 조회
		Page<User> userPage = userRepository.findAll(pageable);

		// 3. OWNER들의 사업자번호 일괄 조회
		List<UUID> ownerUserIds = userPage.getContent().stream()
			.filter(user -> user.getRole() == UserRole.OWNER)
			.map(User::getUserId)
			.toList();

		Map<UUID, String> businessNumberMap = getBusinessNumberMap(ownerUserIds);

		// 4. UserInfo 리스트 변환
		List<UserListResult.UserInfo> members = userPage.getContent().stream()
			.map(user -> UserListResult.UserInfo.from(
				user,
				businessNumberMap.getOrDefault(user.getUserId(), null)
			))
			.toList();

		// 5. 결과 반환
		return UserListResult.of(
			members,
			pageable.getPageNumber() + 1,
			userPage.getTotalPages(),
			userPage.getTotalElements(),
			pageable.getPageSize()
		);
	}

	public UserDetailResult getUserDetail(String role, UUID targetUserId) {
		// 1. MASTER 권한 확인
		if (!UserRole.MASTER.name().equals(role)) {
			throw new AppException(UserErrorCode.FORBIDDEN);
		}

		// 2. 사용자 조회
		User user = userRepository.findByUserId(targetUserId)
			.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

		// 3. OWNER인 경우 사업자번호 조회
		String businessNumber = getBusinessNumberIfOwner(targetUserId, user.getRole().name());

		// 4. 결과 반환
		return UserDetailResult.of(user, businessNumber);
	}
	private Map<UUID, String> getBusinessNumberMap(List<UUID> ownerUserIds) {
		if (ownerUserIds.isEmpty()) {
			return Map.of();
		}
		return ownerRepository.findByUserIdIn(ownerUserIds).stream()
			.collect(Collectors.toMap(
				Owner::getUserId,
				Owner::getBusinessNumber
			));
	}

	@Transactional
	public UpdateProfileResult updateProfile(UUID userId, String role, UpdateProfileCommand command) {
		// 1. 사용자 조회
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

		// 2. 휴대폰번호 수정 (CUSTOMER, OWNER 공통)
		if (command.phoneNumber() != null) {
			user.updatePhoneNumber(command.phoneNumber());
		}

		// 3. OWNER인 경우 사업자번호 수정
		String businessNumber = null;
		if (UserRole.OWNER.name().equals(role) && command.businessNumber() != null) {
			Owner owner = ownerRepository.findByUserId(userId)
				.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
			owner.updateBusinessNumber(command.businessNumber());
			businessNumber = owner.getBusinessNumber();
		} else if (UserRole.OWNER.name().equals(role)) {
			// 사업자번호 수정 안 했으면 기존 값 조회
			businessNumber = ownerRepository.findByUserId(userId)
				.map(Owner::getBusinessNumber)
				.orElse(null);
		}

		// 4. 결과 반환
		return UpdateProfileResult.of(user, businessNumber);
	}

	@Transactional
	public void changePassword(UUID userId, ChangePasswordCommand command) {
		// 1. 새 비밀번호 확인 일치 검증
		if (!command.newPassword().equals(command.confirmPassword())) {
			throw new AppException(UserErrorCode.PASSWORD_NOT_MATCH);
		}

		// 2. 사용자 조회
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

		// 3. 현재 비밀번호 검증
		if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
			throw new AppException(UserErrorCode.INVALID_CURRENT_PASSWORD);
		}

		// 4. 새 비밀번호 암호화 후 저장
		user.updatePassword(passwordEncoder.encode(command.newPassword()));
	}
}
