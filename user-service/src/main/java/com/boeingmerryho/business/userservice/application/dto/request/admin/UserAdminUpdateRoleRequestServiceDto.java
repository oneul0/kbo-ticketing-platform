package com.boeingmerryho.business.userservice.application.dto.request.admin;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleRequestServiceDto(Long id, UserRoleType newRole) {

}
