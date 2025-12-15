package com.tablekok.user_service.user.application.dto.result;

import com.tablekok.user_service.auth.domain.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record UserListResult(
	List<UserInfo> members,
	int currentPage,
	int totalPages,
	long totalCount,
	int limit,
	boolean hasNext,
	boolean hasPrev
) {

	public static UserListResult of(
		List<UserInfo> members,
		int currentPage,
		int totalPages,
		long totalCount,
		int limit
	) {
		return UserListResult.builder()
			.members(members)
			.currentPage(currentPage)
			.totalPages(totalPages)
			.totalCount(totalCount)
			.limit(limit)
			.hasNext(currentPage < totalPages)
			.hasPrev(currentPage > 1)
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
		public static UserInfo from(User user, String businessNumber) {
			return UserInfo.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.businessNumber(businessNumber)
				.role(user.getRole().name())
				.isActive(user.isActive())
				.createdAt(user.getCreatedAt())
				.loginCount(user.getLoginCount())
				.build();
		}
	}
}
