package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.exception.AuthDomainErrorCode;
import com.tablekok.user_service.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

	private final UserRepository userRepository;

	public void validateEmailNotDuplicated(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new AppException(AuthDomainErrorCode.DUPLICATE_EMAIL);
		}
	}
}
