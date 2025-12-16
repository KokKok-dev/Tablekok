package com.tablekok.user_service.user.presentation.dto.response;

import com.tablekok.user_service.user.application.dto.result.UserListResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserListResponse(
	List<UserInfo> members,
	PaginationInfo pagination
) {

	public static UserListResponse from(UserListResult result) {
		List<UserInfo> memberList = result.members().stream()
			.map(UserInfo::from)
			.toList();

		PaginationInfo paginationInfo = PaginationInfo.builder()
			.currentPage(result.currentPage())
			.totalPages(result.totalPages())
			.totalCount(result.totalCount())
			.limit(result.limit())
			.hasNext(result.hasNext())
			.hasPrev(result.hasPrev())
			.build();

		return UserListResponse.builder()
			.members(memberList)
			.pagination(paginationInfo)
			.build();
	}

	@Builder
	public record UserInfo(
		UUID userId,
		String username,
		String email,
		String phoneNumber,
		String businessNumber,
		String role,
		boolean isActive,
		LocalDateTime createdAt,
		int loginCount
	) {
		public static UserInfo from(UserListResult.UserInfo result) {
			return UserInfo.builder()
				.userId(result.userId())
				.username(result.username())
				.email(result.email())
				.phoneNumber(result.phoneNumber())
				.businessNumber(result.businessNumber())
				.role(result.role())
				.isActive(result.isActive())
				.createdAt(result.createdAt())
				.loginCount(result.loginCount())
				.build();
		}
	}

	@Builder
	public record PaginationInfo(
		int currentPage,
		int totalPages,
		long totalCount,
		int limit,
		boolean hasNext,
		boolean hasPrev
	) {}
}
