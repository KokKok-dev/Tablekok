package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.application.exception.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordValidator {

	private final PasswordEncoder passwordEncoder;

	public void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new AppException(AuthErrorCode.LOGIN_FAILED);
		}
	}
}
