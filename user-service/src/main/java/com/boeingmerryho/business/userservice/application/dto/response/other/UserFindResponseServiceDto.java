package com.boeingmerryho.business.userservice.application.dto.response.other;

import java.time.LocalDate;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserFindResponseServiceDto(Long id, String email, String username, String uickname, LocalDate birth,
										 UserRoleType role) {

}
