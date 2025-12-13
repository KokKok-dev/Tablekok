package com.tablekok.user_service.auth.domain.service;

import com.tablekok.exception.AppException;
import com.tablekok.user_service.auth.domain.exception.AuthDomainErrorCode;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class BusinessNumberValidator {

	private static final Pattern BUSINESS_NUMBER_PATTERN =
		Pattern.compile("^\\d{3}-\\d{2}-\\d{5}$");

	public void validate(String businessNumber) {
		if (!BUSINESS_NUMBER_PATTERN.matcher(businessNumber).matches()) {
			throw new AppException(AuthDomainErrorCode.INVALID_BUSINESS_NUMBER_FORMAT);
		}
	}
}
