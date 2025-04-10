package com.boeingmerryho.business.userservice.presentation.dto.request.admin;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchRequestDto(
	Long id,
	String username,
	String email,
	String nickname,
	UserRoleType role,
	Boolean isDeleted
) {

}
