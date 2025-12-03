package com.tablekok.user_service.domain.entity;

import com.tablekok.entity.BaseEntity;
import com.tablekok.user_service.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_user", indexes = {
	@Index(name = "idx_user_email", columnList = "email"),
	@Index(name = "idx_user_phone", columnList = "phoneNumber"),
	@Index(name = "idx_user_role", columnList = "role")
})
public class User extends BaseEntity {

	@Id
	@Column(name = "user_id")
	private UUID userId;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, unique = true, length = 20, name = "phone_number")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role;

	@Column(nullable = false, name = "is_active")
	private Boolean isActive;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Column(nullable = false, name = "login_count")
	private Integer loginCount;

	// Owner와의 1:1 관계 추가
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Owner owner;

	@Builder
	public User(String email, String password, String name, String phoneNumber, UserRole role) {
		this.userId = UUID.randomUUID();
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.role = role != null ? role : UserRole.CUSTOMER;
		this.isActive = true;
		this.loginCount = 0;
	}

	// 비즈니스 메서드들
	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}

	public void updateProfile(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public void updateLoginInfo() {
		this.lastLoginAt = LocalDateTime.now();
		this.loginCount++;
	}

	public void deactivate() {
		this.isActive = false;
	}

	public void activate() {
		this.isActive = true;
	}

	public boolean isAccountActive() {
		return this.isActive && !isDeleted();
	}

	// 권한 체크 유틸리티 메서드
	public boolean isCustomer() {
		return this.role == UserRole.CUSTOMER;
	}

	public boolean isOwner() {
		return this.role == UserRole.OWNER;
	}

	public boolean isMaster() {
		return this.role == UserRole.MASTER;
	}

	// Owner 연관관계 메서드
	public void assignOwner(Owner owner) {
		this.owner = owner;
		this.role = UserRole.OWNER;  // 역할을 OWNER로 변경
	}

	public String getBusinessNumber() {
		return owner != null ? owner.getBusinessNumber() : null;
	}
}
