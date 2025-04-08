package com.boeingmerryho.business.userservice.presentation.dto.response;

import java.time.LocalDate;

import com.boeingmerryho.business.userservice.domain.UserRoleType;

public record UserFindResponseDto(Long id, String email, String username, String uickname, LocalDate birth,
								  UserRoleType role) {

}
