package com.boeingmerryho.business.userservice.presentation.dto.response;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminSearchResponseDto(Long id, String username, String slackId, UserRoleType role,
										 Boolean isDeleted) {

}
