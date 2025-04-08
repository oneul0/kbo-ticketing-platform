package com.boeingmerryho.business.userservice.domain;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private LocalDate birth;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private UserRoleType role = UserRoleType.NORMAL;

	public void updateRoleType(UserRoleType role) {
		this.role = role;
	}

	public void deleteRoleType() {
		this.role = UserRoleType.NORMAL;
	}

	public void updateUsername(String username) {
		this.username = username;
	}

	public void updatePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateBirth(LocalDate birth) {
		this.birth = birth;
	}
}
