package com.boeingmerryho.business.userservice.application.dto.request;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserAdminUpdateRoleRequestServiceDto(Long id, UserRoleType newRole) {

}
