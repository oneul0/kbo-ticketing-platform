package com.boeingmerryho.business.userservice.application.dto.request.other;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UsersRequestServiceDto(UserRoleType role, Boolean isDeleted) {
}
