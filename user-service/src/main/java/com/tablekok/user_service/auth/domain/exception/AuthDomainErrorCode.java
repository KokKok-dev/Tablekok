package com.tablekok.user_service.auth.domain.exception;

import com.tablekok.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Auth Domain 계층 전용 ErrorCode
 * ✅ gashine20 피드백 반영: Domain 계층용 네이밍으로 변경
 *
 * Domain 계층에서 발생하는 비즈니스 검증 에러 정의
 * Common의 GlobalExceptionHandler에서 자동으로 처리됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum AuthDomainErrorCode implements ErrorCode {

	// ========== 사용자 검증 관련 에러 (4XX) ==========

	INVALID_EMAIL("AD001", "이메일은 필수 입력 값이며 올바른 형식이어야 합니다.", HttpStatus.BAD_REQUEST),
	EMAIL_TOO_LONG("AD002", "이메일은 100자를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),

	INVALID_NAME("AD003", "이름은 2자 이상 50자 이하로 입력해주세요.", HttpStatus.BAD_REQUEST),
	INVALID_NAME_FORMAT("AD004", "이름은 한글, 영문, 공백만 허용됩니다.", HttpStatus.BAD_REQUEST),

	INVALID_PASSWORD("AD005", "비밀번호는 8자 이상 20자 이하로 입력해주세요.", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD_FORMAT("AD006", "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),

	INVALID_PHONE_NUMBER("AD007", "휴대폰번호는 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),
	INVALID_PHONE_FORMAT("AD008", "올바른 휴대폰번호 형식이 아닙니다. (01X + 8~9자리 숫자)", HttpStatus.BAD_REQUEST),

	// ========== 중복 관련 에러 (409) ==========

	DUPLICATE_EMAIL("AD100", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT),
	DUPLICATE_PHONE_NUMBER("AD101", "이미 등록된 휴대폰번호입니다.", HttpStatus.CONFLICT),
	DUPLICATE_BUSINESS_NUMBER("AD102", "이미 등록된 사업자번호입니다.", HttpStatus.CONFLICT),

	// ========== 사업자번호 관련 에러 (4XX) ==========

	INVALID_BUSINESS_NUMBER("AD200", "사업자번호는 필수 입력 값입니다.", HttpStatus.BAD_REQUEST),
	INVALID_BUSINESS_NUMBER_FORMAT("AD201", "사업자번호는 10자리 숫자여야 합니다.", HttpStatus.BAD_REQUEST),
	INVALID_BUSINESS_NUMBER_CHECKSUM("AD202", "유효하지 않은 사업자번호입니다.", HttpStatus.BAD_REQUEST),

	// ========== 인증 관련 에러 (401/403) ==========

	USER_NOT_FOUND("AD300", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INVALID_CREDENTIALS("AD301", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
	ACCOUNT_DEACTIVATED("AD302", "비활성화된 계정입니다.", HttpStatus.FORBIDDEN),

	// ========== Owner 관련 에러 (4XX) ==========

	INVALID_USER_FOR_OWNER("AD400", "Owner 역할의 User만 Owner 엔티티를 생성할 수 있습니다.", HttpStatus.BAD_REQUEST),
	USER_REQUIRED("AD401", "User는 필수입니다.", HttpStatus.BAD_REQUEST),

	// ========== 내부 서버 에러 (5XX) ==========

	PASSWORD_ENCODING_ERROR("AD500", "비밀번호 암호화 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;
}
