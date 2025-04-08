package com.boeingmerryho.business.userservice.domain;

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
}
