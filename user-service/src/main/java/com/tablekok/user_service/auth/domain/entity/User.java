package com.tablekok.user_service.auth.domain.entity;

import com.tablekok.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_id", columnDefinition = "UUID")
	private UUID userId;

	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password", nullable = false, length = 100)
	private String password;

	@Column(name = "name", nullable = false, length = 50)
	private String username;

	@Column(name = "phone_number", nullable = false, length = 15)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private UserRole role;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "login_count", nullable = false)
	private int loginCount;

	public static User create(
		String email,
		String password,
		String username,
		String phoneNumber,
		UserRole role
	) {
		return User.builder()
			.email(email)
			.password(password)
			.username(username)
			.phoneNumber(phoneNumber)
			.role(role)
			.isActive(true)
			.loginCount(0)
			.build();
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
