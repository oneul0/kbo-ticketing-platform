package com.boeingmerryho.business.userservice.application.dto.response;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleResponseServiceDto(Long id, UserRoleType role, UserRoleType newRole) {

}
