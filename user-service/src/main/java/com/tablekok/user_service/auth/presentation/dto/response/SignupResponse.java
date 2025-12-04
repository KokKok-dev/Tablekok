// auth/presentation/dto/response/SignupResponse.java
package com.tablekok.user_service.auth.presentation.dto.response;

import com.tablekok.user_service.auth.application.dto.SignupResult;
import com.tablekok.user_service.auth.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/**
 * 회원가입 응답 DTO
 * Customer/Owner 회원가입 공통 사용
 *
 * API 명세서 응답 형식 정확 매핑
 */
@Schema(description = "회원가입 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public record SignupResponse(

	@Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	String accessToken,

	@Schema(description = "사용자 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
	UUID userId,

	@Schema(description = "사용자 이름", example = "홍길동")
	String username,

	@Schema(description = "이메일 주소", example = "customer@example.com")
	String email,

	@Schema(description = "휴대폰번호 (하이픈 제거)", example = "01012345678")
	String phone,

	@Schema(description = "사용자 역할", example = "CUSTOMER", allowableValues = {"CUSTOMER", "OWNER", "MASTER"})
	String role,

	@Schema(description = "사업자번호 (Owner인 경우만)", example = "1234567890")
	String businessNumber
) {

	/**
	 * Application Layer Result → Presentation Response 변환
	 *
	 * @param signupResult Application Layer 결과
	 * @return SignupResponse (Presentation Layer DTO)
	 */
	public static SignupResponse from(SignupResult signupResult) {
		return new SignupResponse(
			signupResult.accessToken(),
			signupResult.userId(),
			signupResult.username(),
			signupResult.email(),
			signupResult.phone(),
			signupResult.role().name(),  // Enum을 String으로 변환
			signupResult.businessNumber()  // Customer는 null, Owner는 사업자번호
		);
	}

	/**
	 * Customer 회원가입 응답 생성
	 * 사업자번호 없음을 명시
	 *
	 * @param signupResult Application Layer 결과
	 * @return Customer 회원가입 응답
	 */
	public static SignupResponse fromCustomer(SignupResult signupResult) {
		return new SignupResponse(
			signupResult.accessToken(),
			signupResult.userId(),
			signupResult.username(),
			signupResult.email(),
			signupResult.phone(),
			UserRole.CUSTOMER.name(),
			null  // Customer는 사업자번호 없음
		);
	}

	/**
	 * Owner 회원가입 응답 생성
	 * 사업자번호 포함
	 *
	 * @param signupResult Application Layer 결과
	 * @return Owner 회원가입 응답
	 */
	public static SignupResponse fromOwner(SignupResult signupResult) {
		return new SignupResponse(
			signupResult.accessToken(),
			signupResult.userId(),
			signupResult.username(),
			signupResult.email(),
			signupResult.phone(),
			UserRole.OWNER.name(),
			signupResult.businessNumber()  // Owner 사업자번호 포함
		);
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isCustomer() {
		return "CUSTOMER".equals(role);
	}

	/**
	 * 역할 확인 편의 메서드
	 */
	public boolean isOwner() {
		return "OWNER".equals(role);
	}

	/**
	 * 사업자번호 존재 여부 확인
	 */
	public boolean hasBusinessNumber() {
		return businessNumber != null && !businessNumber.trim().isEmpty();
	}

	/**
	 * 마스킹된 액세스 토큰 반환 (로깅용)
	 */
	public String getMaskedAccessToken() {
		if (accessToken == null || accessToken.length() <= 10) {
			return "***";
		}

		return accessToken.substring(0, 10) + "..." +
			accessToken.substring(accessToken.length() - 4);
	}

	/**
	 * 디버깅용 문자열 (액세스 토큰 마스킹)
	 */
	@Override
	public String toString() {
		return "SignupResponse{" +
			"accessToken='" + getMaskedAccessToken() + '\'' +
			", userId=" + userId +
			", username='" + username + '\'' +
			", email='" + (email != null ? email.substring(0, Math.min(3, email.length())) + "***" : "null") + '\'' +
			", phone='" + (phone != null ? phone.substring(0, Math.min(3, phone.length())) + "***" : "null") + '\'' +
			", role='" + role + '\'' +
			", businessNumber='" + (businessNumber != null ? businessNumber.substring(0, Math.min(3, businessNumber.length())) + "***" : "null") + '\'' +
			'}';
	}
}
