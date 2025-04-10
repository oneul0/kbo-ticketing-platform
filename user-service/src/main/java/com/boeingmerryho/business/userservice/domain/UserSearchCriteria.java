package com.boeingmerryho.business.userservice.domain;

import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminSearchRequestServiceDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSearchCriteria {
	private final Long id;
	private final String username;
	private final String nickname;
	private final String email;
	private final UserRoleType role;
	private final Boolean isDeleted;

	public static UserSearchCriteria fromAdmin(UserAdminSearchRequestServiceDto dto) {
		return UserSearchCriteria.builder()
			.id(dto.id())
			.username(dto.username())
			.nickname(dto.nickname())
			.email(dto.email())
			.role(dto.role())
			.isDeleted(dto.isDeleted())
			.build();
	}
}
