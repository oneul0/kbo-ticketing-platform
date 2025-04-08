package com.boeingmerryho.business.userservice.application.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleResponseServiceDto(Long id, UserRoleType role, UserRoleType newRole) {

}
