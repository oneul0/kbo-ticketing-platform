package com.boeingmerryho.business.userservice.presentation.dto.response.admin;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchResponseDto(Long id, String username, String nickname, String email, UserRoleType role,
										 Boolean isDeleted) {

}
