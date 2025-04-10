package com.boeingmerryho.business.userservice.application.dto.request.admin;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchRequestServiceDto(
	Long id,
	String username,
	String email,
	String nickname,
	UserRoleType role,
	Boolean isDeleted
) {

}
