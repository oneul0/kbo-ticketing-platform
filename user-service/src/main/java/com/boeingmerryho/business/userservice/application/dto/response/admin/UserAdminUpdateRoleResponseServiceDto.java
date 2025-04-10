package com.boeingmerryho.business.userservice.application.dto.response.admin;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleResponseServiceDto(Long id, UserRoleType role, UserRoleType newRole) {

}
